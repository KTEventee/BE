package com.server.eventee.domain.auth.service;

import com.server.eventee.domain.auth.dto.GoogleTokenResponse;
import com.server.eventee.domain.auth.dto.LoginResponse;
import com.server.eventee.domain.auth.dto.OAuthAttributes;

public interface OAuth2TokenService {
  GoogleTokenResponse getAccessToken(String code);

  OAuthAttributes getUserInfo(String accessToken);

  LoginResponse handleLogin(String code);
}
