package com.server.eventee.domain.member.service;

import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.domain.event.repository.MemberEventRepository;
import com.server.eventee.domain.member.converter.MemberConverter;
import com.server.eventee.domain.event.dto.MemberListDto;
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
      Pattern.compile("^profiles/[0-9]+/[A-Za-z0-9\\-]+\\.(jpg|jpeg|png|webp)$");


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

    log.info("===== [Presigned URL 요청] =====");
    log.info("[요청 Member] memberId={}", member.getId());

    if (request == null) {
      log.error("[ERROR] request 객체가 null 입니다");
      throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_INVALID_REQUEST);
    }

    log.info("[요청 바디] contentType='{}', contentLength={}",
        request.getContentType(),
        request.getContentLength()
    );

    // 유효성 검사
    try {
      validateContentType(request.getContentType());
      log.info("[검증] contentType 유효");
    } catch (Exception e) {
      log.error("[검증 실패] contentType='{}'", request.getContentType());
      throw e;
    }

    try {
      validateLength(request.getContentLength());
      log.info("[검증] contentLength 유효");
    } catch (Exception e) {
      log.error("[검증 실패] contentLength={}", request.getContentLength());
      throw e;
    }

    // 확장자 매핑
    String ext = mapExt(request.getContentType());
    log.info("[확장자 매핑] {} → {}", request.getContentType(), ext);

    // S3 key 생성
    String key = props.getKeyPrefix() + member.getId() + "/" + UUID.randomUUID() + ext;
    log.info("[S3 Key 생성] key={}", key);

    // Presigned URL 생성
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
      log.error("[ERROR] Presigned URL 생성 실패: {}", e.getMessage(), e);
      throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_PRESIGNED_ERROR);
    }

    log.info("[Presigned URL 생성 완료]");
    log.info(" - URL: {}", presigned.url());
    log.info(" - Expires: {} sec", 300);
    log.info("==============================");

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

  @Override
  @Transactional
  public String confirmUpload(Member member, ConfirmUploadRequest request) {

    log.info("===== [업로드 확정 요청 시작] =====");
    log.info("[요청자] memberId={}", member.getId());

    log.info("[Confirm 요청 바디] key={}, contentType={}, size={}",
        request.getKey(),
        request.getContentType(),
        request.getSize()
    );

    try {
      ensureKeyOwnedByMember(member, request.getKey());
      log.info("[Key 소유자 검증 통과]");
    } catch (Exception e) {
      log.error("[Key 검증 실패] key={}", request.getKey());
      throw e;
    }

    try {
      validateContentType(request.getContentType());
      log.info("[ContentType 검증 통과]");
    } catch (Exception e) {
      log.error("[ContentType 검증 실패] {}", request.getContentType());
      throw e;
    }

    try {
      validateLength(request.getSize());
      log.info("[ContentLength 검증 통과]");
    } catch (Exception e) {
      log.error("[ContentLength 검증 실패] {}", request.getSize());
      throw e;
    }

    HeadObjectResponse head;
    try {
      head = s3.headObject(HeadObjectRequest.builder()
          .bucket(props.getBucket())
          .key(request.getKey())
          .build());
      log.info("[S3 HeadObject 조회 성공] contentType={}", head.contentType());
    } catch (Exception e) {
      log.error("[S3 HeadObject 조회 실패] key={}", request.getKey());
      throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_NOT_FOUND);
    }

    // ContentType 체크
    if (!StringUtils.equals(head.contentType(), request.getContentType())) {
      log.error("[S3 ContentType 불일치] head={}, request={}",
          head.contentType(),
          request.getContentType());
      throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_CONTENT_TYPE_MISMATCH);
    }

    // 기존 이미지 있으면 삭제
    if (StringUtils.isNotBlank(member.getProfileImageUrl())) {
      log.info("[기존 이미지 삭제] url={}", member.getProfileImageUrl());
      deleteIfExists(objectKeyFromUrl(member.getProfileImageUrl()));
    }

    String imageUrl = buildPublicUrl(request.getKey());
    member.updateProfileImage(request.getKey(), imageUrl);
    memberRepository.save(member);

    log.info("[프로필 이미지 반영 완료] memberId={}, newUrl={}", member.getId(), imageUrl);
    log.info("===== [업로드 확정 요청 종료] =====");

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

    if (!key.startsWith(expectedPrefix)) {
      log.error("[Key Prefix 불일치] expectedPrefix={}, key={}", expectedPrefix, key);
      throw new MemberHandler(MemberErrorStatus.MEMBER_IMAGE_INVALID_KEY);
    }

    if (!KEY_PATTERN.matcher(key).matches()) {
      log.error("[Key Pattern 불일치] key={}", key);
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
