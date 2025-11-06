package com.server.eventee.global.token;


import com.server.eventee.domain.member.model.Member;
import com.server.eventee.global.token.vo.AccessToken;
import com.server.eventee.global.token.vo.RefreshToken;

public interface TokenProvider {
    AccessToken generateAccessToken(Member member);

    RefreshToken generateRefreshToken(Member member);
}
