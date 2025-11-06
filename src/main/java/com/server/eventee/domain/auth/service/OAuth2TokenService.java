package com.server.eventee.domain.auth.service;

public interface OAuth2TokenService {
  GoogleTokenResponse getAccessToken(String code);

  OAuthAttributes getUserInfo(String accessToken);

  LoginResponse handleLogin(String code);

  LoginResponse handleTestLogin(String code);
}
