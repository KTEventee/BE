package com.server.eventee.domain.file.service;

import com.server.eventee.domain.event.excepiton.EventHandler;
import com.server.eventee.domain.event.excepiton.status.EventErrorStatus;
import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.repository.EventRepository;
import com.server.eventee.domain.file.dto.FileRequest.FileConfirmRequest;
import com.server.eventee.domain.file.dto.FileRequest.FileDeleteRequest;
import com.server.eventee.domain.file.dto.FileRequest.FileUploadRequest;
import com.server.eventee.domain.file.dto.FileUploadResponse;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.group.repository.GroupRepository;
import com.server.eventee.domain.member.exception.MemberHandler;
import com.server.eventee.domain.member.exception.status.MemberErrorStatus;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.member.repository.MemberRepository;
import com.server.eventee.global.aws.S3Props;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final S3Client s3;
  private final S3Presigner presigner;
  private final S3Props props;

  private final MemberRepository memberRepository;
  private final GroupRepository groupRepository;
  private final EventRepository eventRepository;

  @Override
  @Transactional(readOnly = true)
  public FileUploadResponse createPresignedUrl(FileUploadRequest request) {

    validateContentType(request.getContentType());
    validateLength(request.getContentLength());

    String ext = mapExt(request.getContentType());
    String key = buildKey(request.getType(), request.getRefId(), ext);

    PresignedPutObjectRequest presigned;
    try {
      presigned = presigner.presignPutObject(b -> b
          .signatureDuration(Duration.ofMinutes(5))
          .putObjectRequest(r -> r
              .bucket(props.getBucket())
              .key(key)
              .contentType(request.getContentType()))
      );
    } catch (Exception e) {
      throw new RuntimeException("Presigned URL 생성 실패", e);
    }

    return FileUploadResponse.builder()
        .presignedUrl(presigned.url().toString())
        .publicUrl(buildPublicUrl(key))
        .build();
  }

  @Override
  @Transactional
  public String confirmUpload(Member member, FileConfirmRequest request) {

    String key = extractKeyFromUrl(request.getFileUrl());

    validateObjectExists(key);

    String url = request.getFileUrl();

    switch (request.getType().toUpperCase()) {

      case "PROFILE" -> {
        member.updateProfileImage(key, url);
        memberRepository.save(member);
      }

      case "GROUP" -> {
        Group group = groupRepository.findById(request.getRefId())
            .orElseThrow(() -> new EventHandler(EventErrorStatus.GROUP_NOT_FOUND));
        group.updateGroupImg(url);
      }

      case "EVENT" -> {
        Event event = eventRepository.findById(request.getRefId())
            .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));
        event.updateThumbnail(url);
      }

      case "POST" -> {
        // TODO: post 이미지 있을 경우 여기에 반영해야됨
      }

      case "COMMENT" -> {
        // TODO: comment 이미지 있을 경우 여기에 반영해야딤
      }

      default -> throw new RuntimeException("지원되지 않는 type");
    }

    return url;
  }

  @Override
  @Transactional
  public void deleteFile(FileDeleteRequest request) {

    switch (request.getType().toUpperCase()) {

      case "PROFILE" -> {
        Member member = memberRepository.findById(request.getRefId())
            .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        if (StringUtils.isNotBlank(member.getProfileImageUrl())) {
          deleteObject(extractKeyFromUrl(member.getProfileImageUrl()));
          member.clearProfileImage();
        }
      }

      case "GROUP" -> {
        Group group = groupRepository.findById(request.getRefId())
            .orElseThrow(() -> new EventHandler(EventErrorStatus.GROUP_NOT_FOUND));

        if (StringUtils.isNotBlank(group.getGroupImg())) {
          deleteObject(extractKeyFromUrl(group.getGroupImg()));
          group.updateGroupImg(null);
        }
      }

      case "EVENT" -> {
        Event event = eventRepository.findById(request.getRefId())
            .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

        if (StringUtils.isNotBlank(event.getThumbnailUrl())) {
          deleteObject(extractKeyFromUrl(event.getThumbnailUrl()));
          event.updateThumbnail(null);
        }
      }

      default -> throw new EventHandler(EventErrorStatus.SUPPORTED_NOT_TYPE);
    }
  }

  /* ---------------------------------------------------------------------------
     내부 유틸
  --------------------------------------------------------------------------- */

  private void validateContentType(String contentType) {
    if (props.getAllowedContentTypes().stream()
        .noneMatch(allowed -> allowed.equalsIgnoreCase(contentType))) {
      throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_INVALID_CONTENT_TYPE);
    }
  }

  private void validateLength(long len) {
    if (len <= 0 || len > props.getMaxUploadSizeBytes()) {
      throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_INVALID_SIZE);
    }
  }

  private String mapExt(String contentType) {
    return switch (contentType) {
      case "image/jpeg" -> ".jpg";
      case "image/png" -> ".png";
      case "image/webp" -> ".webp";
      default -> throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_INVALID_CONTENT_TYPE);
    };
  }

  private String buildKey(String type, Long refId, String ext) {
    return switch (type.toUpperCase()) {
      case "PROFILE" -> "profiles/" + refId + "/" + UUID.randomUUID() + ext;
      case "GROUP"   -> "groups/" + refId + "/" + UUID.randomUUID() + ext;
      case "EVENT"   -> "events/" + refId + "/" + UUID.randomUUID() + ext;
      case "POST"    -> "posts/" + refId + "/" + UUID.randomUUID() + ext;
      case "COMMENT" -> "comments/" + refId + "/" + UUID.randomUUID() + ext;
      default -> throw new EventHandler(EventErrorStatus.SUPPORTED_NOT_TYPE);
    };
  }

  private void validateObjectExists(String key) {
    try {
      s3.headObject(HeadObjectRequest.builder()
          .bucket(props.getBucket())
          .key(key)
          .build());
    } catch (Exception e) {
      throw new EventHandler(EventErrorStatus.FILE_NOT_FOUND);
    }
  }

  private void deleteObject(String key) {
    try {
      s3.deleteObject(DeleteObjectRequest.builder()
          .bucket(props.getBucket())
          .key(key)
          .build());
    } catch (Exception ignored) {
    }
  }

  private String extractKeyFromUrl(String url) {
    return url.substring(url.indexOf(".amazonaws.com/") + ".amazonaws.com/".length());
  }

  private String buildPublicUrl(String key) {
    return "https://" + props.getBucket() + ".s3." + props.getRegion() + ".amazonaws.com/" + key;
  }
}
