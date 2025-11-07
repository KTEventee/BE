package com.server.eventee.domain.member.service;

import com.server.eventee.domain.member.model.Member;
import jakarta.validation.constraints.NotBlank;

public interface MemberService {

  boolean confirmNickName(Member member, String nickname);
}
