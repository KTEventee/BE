package com.server.eventee.domain.auth.service;

import com.server.eventee.domain.auth.dto.GoogleTokenResponse;
import com.server.eventee.domain.auth.dto.LoginResponse;
import com.server.eventee.domain.auth.dto.OAuthAttributes;
import com.server.eventee.domain.member.model.Member;

public interface OAuth2TokenService {
  GoogleTokenResponse getAccessToken(String code);

  OAuthAttributes getUserInfo(String accessToken);

  LoginResponse handleLogin(String code);
  void logout(String refreshToken);


}
