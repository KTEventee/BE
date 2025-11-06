package com.server.eventee.domain.auth.service;

import com.server.eventee.domain.auth.dto.GoogleTokenResponse;
import com.server.eventee.domain.auth.dto.LoginResponse;
import com.server.eventee.domain.auth.dto.OAuthAttributes;
import com.server.eventee.domain.auth.exception.AuthHandler;
import com.server.eventee.domain.auth.exception.status.AuthErrorStatus;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.member.model.Role;
import com.server.eventee.domain.member.repository.MemberRepository;
import com.server.eventee.global.token.TokenProvider;
import com.server.eventee.global.token.vo.AccessToken;
import com.server.eventee.global.token.vo.RefreshToken;
import com.server.eventee.global.token.vo.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleTokenService implements OAuth2TokenService {

  private final RestTemplate restTemplate;
  private final MemberRepository memberRepository;
  private final TokenProvider tokenProvider;  // ✅ 팀원의 JWT Provider 사용
  private final RedisTemplate<String, String> redisTemplate;

  private static final String REFRESH_TOKEN_PREFIX = "REFRESH:";
  private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 2;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String clientSecret;

  @Value("${spring.security.oauth2.client.provider.google.token-uri}")
  private String tokenUri;

  @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
  private String userInfoUri;

  @Value("${spring.security.oauth2.client.registration.google.authorization-grant-type}")
  private String grantType;

  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  private String redirectUri;

  @Override
  @Transactional
  public LoginResponse handleLogin(String code) {
    String decode = URLDecoder.decode(code, StandardCharsets.UTF_8);
    String accessToken = getAccessToken(decode).getAccessToken();
    OAuthAttributes attributes = getUserInfo(accessToken);

    Member member = memberRepository.findBySocialId(attributes.getSub())
        .orElseGet(() -> createNewMember(attributes));

    // ✅ 팀원 JwtProvider를 이용해 토큰 발급
    AccessToken newAccessToken = tokenProvider.generateAccessToken(member);
    RefreshToken newRefreshToken = tokenProvider.generateRefreshToken(member);

    // ✅ Redis에 Refresh Token 저장
    redisTemplate.opsForValue().set(
        REFRESH_TOKEN_PREFIX + member.getSocialId(),
        newRefreshToken.token(),
        REFRESH_TOKEN_EXPIRE_TIME,
        TimeUnit.MILLISECONDS
    );

    log.info("AccessToken: {}", newAccessToken.token());
    log.info("RefreshToken: {}", newRefreshToken.token());

    TokenResponse tokenResponse = TokenResponse.of(newAccessToken, newRefreshToken);

    // ✅ 기존 LoginResponse에 통합
    return new LoginResponse(
        member.getEmail(),
        tokenResponse.accessToken().token(),
        member.getSocialId(),
        tokenResponse.refreshToken().token()
    );
  }

  @Override
  @Transactional
  public LoginResponse handleTestLogin(String code) {
    Member member = memberRepository.findBySocialId("test")
        .orElseThrow(() -> new AuthHandler(AuthErrorStatus._TEST_LOGIN_FAIL));

    AccessToken newAccessToken = tokenProvider.generateAccessToken(member);
    RefreshToken newRefreshToken = tokenProvider.generateRefreshToken(member);

    redisTemplate.opsForValue().set(
        REFRESH_TOKEN_PREFIX + member.getSocialId(),
        newRefreshToken.token(),
        REFRESH_TOKEN_EXPIRE_TIME,
        TimeUnit.MILLISECONDS
    );

    TokenResponse tokenResponse = TokenResponse.of(newAccessToken, newRefreshToken);

    return new LoginResponse(
        member.getEmail(),
        tokenResponse.accessToken().token(),
        member.getSocialId(),
        tokenResponse.refreshToken().token()
    );
  }

  @Transactional
  public Member createNewMember(OAuthAttributes attributes) {
    Member newMember = attributes.toEntity();
    newMember.setRole(Role.ROLE_USER);
    newMember.setCredit(100);
    return memberRepository.save(newMember);
  }

  @Transactional
  public GoogleTokenResponse getAccessToken(String code) {
    String tokenUrl = UriComponentsBuilder.fromHttpUrl(tokenUri)
        .queryParam("grant_type", grantType)
        .queryParam("client_id", clientId)
        .queryParam("client_secret", clientSecret)
        .queryParam("redirect_uri", redirectUri)
        .queryParam("code", code)
        .build()
        .toUriString();

    try {
      ResponseEntity<GoogleTokenResponse> response =
          restTemplate.exchange(tokenUrl, HttpMethod.POST, null, GoogleTokenResponse.class);
      return response.getBody();
    } catch (Exception e) {
      log.error("구글 액세스 토큰 요청 실패: {}", e.getMessage(), e);
      throw new AuthHandler(AuthErrorStatus._TOKEN_FAIL);
    }
  }

  @Override
  @Transactional
  public OAuthAttributes getUserInfo(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> request = new HttpEntity<>(headers);

    try {
      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
          userInfoUri, HttpMethod.GET, request, new ParameterizedTypeReference<>() {});
      Map<String, Object> attributes = response.getBody();

      return OAuthAttributes.builder()
          .attributes(attributes)
          .email((String) attributes.get("email"))
          .sub((String) attributes.get("sub"))
          .build();

    } catch (Exception e) {
      throw new AuthHandler(AuthErrorStatus._USER_INFO_FAIL);
    }
  }
}
