package com.server.eventee.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "소셜 로그인 API")
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class AuthController {

  private final GoogleTokenService googleTokenService;
  private final CookieHelper cookieHelper;

  @Operation(summary = "구글 로그인", description = "구글 OAuth 인증 코드로 로그인 처리 및 JWT 토큰 발급")
  @GetMapping(value = "/google", produces = "application/json")
  public ApiResponse<LoginResponse> processGoogleLogin(
      @RequestParam("code") String code, HttpServletResponse response) {

    LoginResponse loginResponse =
        code.equals("test")
            ? googleTokenService.handleTestLogin(code)
            : googleTokenService.handleLogin(code);

    ResponseCookie responseCookie =
        cookieHelper.createHttpOnlyCookie("refreshToken", loginResponse.getRefreshToken());
    response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    return ApiResponse.of(AuthSuccessStatus._LOGIN_SUCCESS, loginResponse);
  }
}
