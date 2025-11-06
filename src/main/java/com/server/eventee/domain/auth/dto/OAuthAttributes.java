package com.server.eventee.domain.auth.dto;

import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.member.model.Role;

import java.util.Map;

public record OAuthAttributes(
    Map<String, Object> attributes,
    String email,
    String sub
) {
  public Member toEntity() {
    return Member.builder()
        .email(email)
        .socialId(sub)
        .role(Role.ROLE_USER)
        .build();
  }

  public static OAuthAttributes of(Map<String, Object> attributes) {
    return new OAuthAttributes(
        attributes,
        (String) attributes.get("email"),
        (String) attributes.get("sub")
    );
  }
}
