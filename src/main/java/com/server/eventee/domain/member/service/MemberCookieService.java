package com.server.eventee.domain.member.service;

import com.server.eventee.global.token.vo.AccessToken;
import com.server.eventee.global.token.vo.TokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import static com.server.eventee.global.token.JwtProperties.*;


@Service
@RequiredArgsConstructor
public class MemberCookieService {

    public void addTokenCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        // Access Token 쿠키
        addAccessTokenCookies(response, tokenResponse);
        addRefreshCookies(response, tokenResponse);
    }


    //      .secure(true)              // HTTPS 전용
    //    .sameSite("None")        // CSRF 방어
//    .sameSite("Lax")
//    .secure(false)

    private void addAccessTokenCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        ResponseCookie accessCookie = ResponseCookie.from(JWT_ACCESS_TOKEN_COOKIE_NAME, tokenResponse.accessToken().token())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(ACCESS_TOKEN_EXPIRE_TIME)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }
    private void addAccessTokenCookies(HttpServletResponse response, AccessToken accessToken) {
        ResponseCookie accessCookie = ResponseCookie.from(JWT_ACCESS_TOKEN_COOKIE_NAME, accessToken.token())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(ACCESS_TOKEN_EXPIRE_TIME)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    public void addAccessTokenCookies(HttpServletResponse response, String accessToken) {
        ResponseCookie accessCookie = ResponseCookie.from(JWT_ACCESS_TOKEN_COOKIE_NAME, accessToken)
                .httpOnly(true)
//                .secure(true)
                .secure(false)
                .path("/")
                .maxAge(ACCESS_TOKEN_EXPIRE_TIME)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    private void addRefreshCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        ResponseCookie refreshCookie = ResponseCookie.from(JWT_REFRESH_TOKEN_COOKIE_NAME, tokenResponse.refreshToken().token())
                .httpOnly(true)
//                .secure(true)
                .secure(false)
                .path("/")
                .maxAge(REFRESH_TOKEN_EXPIRE_TIME)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

        public void deleteCookie(HttpServletResponse response, TokenResponse tokenResponse){
            expiredCookie(response,tokenResponse);
        }

        private void expiredCookie(HttpServletResponse response, TokenResponse tokenResponse){
            ResponseCookie deleteAccess = ResponseCookie.from(JWT_ACCESS_TOKEN_COOKIE_NAME, "")
                    .httpOnly(true)
//                    .secure(true)
                    .secure(false)
                    .path("/")
                    .maxAge(0)
                    .sameSite("Lax")
                    .build();

            // 2) REFRESH_TOKEN 쿠키 삭제
            ResponseCookie deleteRefresh = ResponseCookie.from(JWT_REFRESH_TOKEN_COOKIE_NAME, "")
                    .httpOnly(true)
//                    .secure(true)
                    .secure(false)
                    .path("/")
                    .maxAge(0)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, deleteAccess.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, deleteRefresh.toString());
        }

}

