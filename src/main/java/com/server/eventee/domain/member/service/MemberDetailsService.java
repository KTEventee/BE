package com.server.eventee.domain.member.service;

import com.server.eventee.domain.member.dto.MemberAuthContext;
import com.server.eventee.domain.member.dto.MemberDetails;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.member.repository.MemberRepository;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Spring Security에서 인증된 사용자를 로드하기 위한 서비스.
 * - JWT 인증 과정에서 이메일을 기반으로 MemberDetails를 생성함.
 */
@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) {
    Member member = memberRepository.findMemberByEmail(email)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    return new MemberDetails(
        MemberAuthContext.of(member),
        member
    );
  }
}
