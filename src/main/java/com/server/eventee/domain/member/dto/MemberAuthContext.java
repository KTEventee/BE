package com.server.eventee.domain.member.dto;

import com.server.eventee.domain.member.model.Member;
import lombok.Builder;

@Builder
public record MemberAuthContext(
    Long id,
    String nickname,
    String email,
    String role,
    String profileImageKey,
    String socialId
) {
    public static MemberAuthContext of(Member member) {
        return MemberAuthContext.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .email(member.getEmail())
            .role(member.getRole().name())
            .profileImageKey(member.getProfileImageKey())
            .socialId(member.getSocialId())
            .build();
    }
}
