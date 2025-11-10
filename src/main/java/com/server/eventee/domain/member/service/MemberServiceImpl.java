package com.server.eventee.domain.member.service;

import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.domain.event.repository.MemberEventRepository;
import com.server.eventee.domain.member.converter.MemberConverter;
import com.server.eventee.domain.member.dto.MemberMyPageResponse;
import com.server.eventee.domain.member.dto.MemberProfileImageDto.*;
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
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final MemberEventRepository memberEventRepository;
  private final MemberConverter memberConverter;

  private final S3Client s3;
  private final S3Presigner presigner;
  private final S3Props props;

  private static final Pattern KEY_PATTERN =
      Pattern.compile("^[a-z0-9\\-/]+\\.(jpg|jpeg|png|webp)$");

  // 닉네임 중복 확인 및 변경
  @Override
  @Transactional
  public String checkAndUpdateNickname(Member member, String nickname) {
    if (nickname == null || nickname.isBlank()) {
      throw new MemberHandler(MemberErrorStatus.MEMBER_NICKNAME_NULL);
    }

    String trimmed = nickname.trim();

    if (trimmed.equals(member.getNickname())) {
      log.info("[닉네임 변경 불필요] 동일한 닉네임 요청 nickname={}, memberId={}", trimmed, member.getId());
      return trimmed;
    }

    if (memberRepository.existsByNickname(trimmed)) {
      throw new MemberHandler(MemberErrorStatus.MEMBER_NICKNAME_DUPLICATED);
    }

    member.updateNickname(trimmed);
    memberRepository.save(member);
    log.info("[닉네임 변경 완료] memberId={}, newNickname={}", member.getId(), trimmed);
    return trimmed;
  }

  // Presigned URL (PUT) 발급
  @Override
  @Transactional(readOnly = true)
  public PresignedUrlResponse createPresignedUrl(Member member, UploadIntentRequest request) {
    validateContentType(request.getContentType());
    validateLength(request.getContentLength());

    String ext = mapExt(request.getContentType());
    String key = props.getKeyPrefix() + member.getId() + "/" + UUID.randomUUID() + ext;

    PresignedPutObjectRequest presigned = presigner.presignPutObject(b -> b
        .signatureDuration(Duration.ofMinutes(5))
        .putObjectRequest(r -> r
            .bucket(props.getBucket())
            .key(key)
            .contentType(request.getContentType()))
    );

    log.info("[Presigned URL 생성] memberId={}, key={}", member.getId(), key);

    return PresignedUrlResponse.builder()
        .url(presigned.url().toString())
        .key(key)
        .expiresIn(300)
        .build();
  }

  // 마이페이지 정보 조회
  @Override
  @Transactional(readOnly = true)
  public MemberMyPageResponse getMyPageInfo(Member member) {
    List<MemberEvent> memberEvents = memberEventRepository.findAllByMemberAndIsDeletedFalse(member);
    List<Event> joinedEvents = memberEvents.stream()
        .map(MemberEvent::getEvent)
        .toList();
    return memberConverter.toResponse(member, joinedEvents);
  }

  // 업로드 완료 후 DB 반영
  @Override
  @Transactional
  public String confirmUpload(Member member, ConfirmUploadRequest request) {
    ensureKeyOwnedByMember(member, request.getKey());
    validateContentType(request.getContentType());
    validateLength(request.getSize());

    HeadObjectResponse head;
    try {
      head = s3.headObject(HeadObjectRequest.builder()
          .bucket(props.getBucket())
          .key(request.getKey())
          .build());
    } catch (Exception e) {
      throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_NOT_FOUND);
    }

    if (!StringUtils.equals(head.contentType(), request.getContentType())) {
      throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_CONTENT_TYPE_MISMATCH);
    }

    if (StringUtils.isNotBlank(member.getProfileImageUrl())) {
      deleteIfExists(objectKeyFromUrl(member.getProfileImageUrl()));
    }

    String imageUrl = buildPublicUrl(request.getKey());
    member.updateProfileImage(request.getKey(), imageUrl);
    memberRepository.save(member);

    log.info("[프로필 이미지 확정] memberId={}, url={}", member.getId(), imageUrl);
    return imageUrl;
  }

  // 기존 프로필 이미지 삭제
  @Override
  @Transactional
  public DeleteImageResponse deleteProfileImage(Member member) {
    String prevUrl = member.getProfileImageUrl();
    if (StringUtils.isBlank(prevUrl)) {
      return DeleteImageResponse.builder().status("not_found").build();
    }

    deleteIfExists(objectKeyFromUrl(prevUrl));
    member.clearProfileImage();
    memberRepository.save(member);

    log.info("[프로필 이미지 삭제 완료] memberId={}, prevUrl={}", member.getId(), prevUrl);

    return DeleteImageResponse.builder()
        .previousUrl(prevUrl)
        .status("deleted")
        .build();
  }

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

  private void ensureKeyOwnedByMember(Member member, String key) {
    String expectedPrefix = props.getKeyPrefix() + member.getId() + "/";
    if (!key.startsWith(expectedPrefix) || !KEY_PATTERN.matcher(key).matches()) {
      throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_INVALID_KEY);
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

  private void deleteIfExists(String key) {
    try {
      s3.deleteObject(DeleteObjectRequest.builder()
          .bucket(props.getBucket())
          .key(key)
          .build());
    } catch (Exception ignored) {
    }
  }

  private String objectKeyFromUrl(String url) {
    String s3Domain = props.getBucket() + ".s3." + props.getRegion() + ".amazonaws.com/";
    return url.contains(s3Domain)
        ? StringUtils.substringAfter(url, s3Domain)
        : url;
  }

  private String buildPublicUrl(String key) {
    return "https://" + props.getBucket() + ".s3." + props.getRegion() + ".amazonaws.com/" + key;
  }
}
