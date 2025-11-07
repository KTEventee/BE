package com.server.eventee.domain.member.service;

import com.server.eventee.domain.member.exception.MemberHandler;
import com.server.eventee.domain.member.exception.MemberErrorStatus;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  /**
   * 닉네임 중복 확인
   *
   * @param member   현재 로그인한 회원 (검증은 @CurrentMember에서 처리)
   * @param nickname 중복 확인할 닉네임
   * @return 중복되지 않으면 true
   */
  @Override
  public boolean confirmNickName(Member member, String nickname) {
    if (nickname == null || nickname.isBlank()) {
      throw new MemberHandler(MemberErrorStatus.MEMBER_NICKNAME_NULL);
    }
    boolean exists = memberRepository.existsByNickname(nickname.trim());
    if (exists) {
      throw new MemberHandler(MemberErrorStatus.MEMBER_NICKNAME_DUPLICATED);
    }
    log.info("[닉네임 중복 확인 완료] nickname={}, memberId={}", nickname, member.getId());
    return true;
  }
}
