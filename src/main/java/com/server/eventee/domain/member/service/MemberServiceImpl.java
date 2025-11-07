package com.server.eventee.domain.member.service;

import com.server.eventee.domain.member.exception.MemberErrorStatus;
import com.server.eventee.domain.member.exception.MemberHandler;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  /**
   * @param member   현재 로그인한 회원 (검증은 @CurrentMember에서 처리)
   * @param nickname 변경할 닉네임
   * @return 변경된 닉네임
   */
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

    boolean exists = memberRepository.existsByNickname(trimmed);
    if (exists) {
      throw new MemberHandler(MemberErrorStatus.MEMBER_NICKNAME_DUPLICATED);
    }
    member.updateNickname(trimmed);
    memberRepository.save(member);
    log.info("[닉네임 변경 완료] memberId={}, newNickname={}", member.getId(), trimmed);
    return trimmed;
  }
}
