package com.server.eventee.domain.auth.service;

import com.server.eventee.domain.auth.dto.GoogleTokenResponse;
import com.server.eventee.domain.auth.dto.LoginResponse;
import com.server.eventee.domain.auth.dto.OAuthAttributes;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.member.repository.MemberRepository;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.ErrorCode;
import com.server.eventee.global.token.TokenProvider;
import com.server.eventee.global.token.vo.AccessToken;
import com.server.eventee.global.token.vo.RefreshToken;
import com.server.eventee.global.token.vo.TokenResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleTokenService implements OAuth2TokenService {

  private final RestTemplate restTemplate;
  private final MemberRepository memberRepository;
  private final TokenProvider tokenProvider;
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

  // 구글 로그인 처리
  @Transactional
  @Override
  public LoginResponse handleLogin(String code) {
    try {
      // 인가 코드 URL 디코딩
      String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);

      // access token 발급 요청
      GoogleTokenResponse tokenResponse = getAccessToken(decodedCode);
      String accessToken = tokenResponse.accessToken();

      // 사용자 정보 조회
      OAuthAttributes attributes = getUserInfo(accessToken);

      // 회원 조회 또는 생성
      Member member = memberRepository.findBySocialId(attributes.sub())
          .orElseGet(() -> createNewMember(attributes));

      // JWT 토큰 생성
      AccessToken newAccessToken = tokenProvider.generateAccessToken(member);
      RefreshToken newRefreshToken = tokenProvider.generateRefreshToken(member);

      // Redis에 refresh token 저장
      redisTemplate.opsForValue().set(
          REFRESH_TOKEN_PREFIX + member.getSocialId(),
          newRefreshToken.token(),
          REFRESH_TOKEN_EXPIRE_TIME,
          TimeUnit.MILLISECONDS
      );

      TokenResponse jwtResponse = TokenResponse.of(newAccessToken, newRefreshToken);

      return new LoginResponse(
          member.getEmail(),
          jwtResponse.accessToken().token(),
          member.getSocialId(),
          jwtResponse.refreshToken().token()
      );

    } catch (Exception e) {
      log.error("구글 로그인 처리 중 오류 발생: {}", e.getMessage(), e);
      throw new BaseException(ErrorCode._INTERNAL_SERVER_ERROR);
    }
  }

  @Transactional
  public Member createNewMember(OAuthAttributes attributes) {
    Member newMember = attributes.toEntity();
    return memberRepository.save(newMember);
  }

  // 인가 코드로 구글 AT 요청
  @Transactional
  @Override
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
      throw new BaseException(ErrorCode.INVALID_TOKEN);
    }
  }

  // 구글 AT로 사용자 정보 요청
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

      return OAuthAttributes.of(attributes);
    } catch (Exception e) {
      log.error("구글 사용자 정보 요청 실패: {}", e.getMessage(), e);
      throw new BaseException(ErrorCode.MEMBER_NOT_FOUND);
    }
  }
}
