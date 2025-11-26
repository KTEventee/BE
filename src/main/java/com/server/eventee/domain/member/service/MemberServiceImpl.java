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

}
