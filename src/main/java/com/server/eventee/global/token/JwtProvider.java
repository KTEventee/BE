package com.server.eventee.global.token;


import com.server.eventee.domain.member.dto.MemberDetails;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.member.service.MemberDetailsService;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.ErrorCode;
import com.server.eventee.global.token.vo.AccessToken;
import com.server.eventee.global.token.vo.RefreshToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static com.server.eventee.global.token.JwtProperties.*;


@Getter
@Component
@Slf4j
public class JwtProvider implements TokenProvider {

    private final SecretKey SECRET_KEY;
    private final String ISS = "github.com/cooperationCenter";
    private final MemberDetailsService memberDetailsService;
    private final JwtParser jwtParser;


    public JwtProvider(
            @Value("${jwt.secret}") String SECRET_KEY,
            MemberDetailsService memberDetailsService
    ) {
        byte[] keyBytes = Base64.getDecoder()
                .decode(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        this.SECRET_KEY = new SecretKeySpec(keyBytes, "HmacSHA256");
        this.memberDetailsService = memberDetailsService;
        //fixme jwtParser수정하기
        this.jwtParser = Jwts
                .parser()
                .verifyWith(this.SECRET_KEY)
                .build();
    }


    public AccessToken generateAccessToken(Member member) {
        if (member.getEmail() == null || member.getEmail().isBlank()) {
            return AccessToken.of("");
        }
        return this.generateAccessToken(member.getEmail());
    }

    private AccessToken generateAccessToken(String email) {

        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY.getEncoded());  // Base64 디코딩
        Key key = Keys.hmacShaKeyFor(keyBytes);
        Date now = new Date(); // 한번만 생성
        Date expiry = new Date(now.getTime()+ACCESS_TOKEN_EXPIRE_TIME);

        String token = Jwts.builder()
                .claim("type", "access")
                .issuer(ISS)
                .audience().add(email).and()
                .issuedAt(now)
                .expiration(expiry)
                .signWith(SECRET_KEY)
                .compact();

        log.info("[generateAccessToken] {}", token);
        return AccessToken.of(token);
    }

    public RefreshToken generateRefreshToken(Member member) {
        if (member.getEmail() == null || member.getEmail().isBlank()) {
            return RefreshToken.of("");
        }
        return this.generateRefreshToken(member.getEmail());
    }

    private RefreshToken generateRefreshToken(String email) {

        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY.getEncoded());  // Base64 디코딩
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Date now = new Date(); // 한번만 생성
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);
        Date loginExpiry = new Date(now.getTime() + LOGIN_EXPIRE_TIME);

        String token = Jwts.builder()
                .claim("type", "refresh")
                .claim("loginLimit", loginExpiry)
                .issuer(ISS)
                .audience().add(email).and()
                .issuedAt(now)
                .expiration(expiry)
                .signWith(SECRET_KEY)
                .compact();

        log.info("[generateRefreshToken] {}", token);
        return RefreshToken.of(token);
    }

    public String parseAudience(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);

            if (claims.getPayload()
                    .getExpiration()
                    .before(new Date())) {
                throw new BaseException(ErrorCode.EXPIRED_ACCESS_TOKEN);
            }

            String aud = claims.getPayload()
                    .getAudience()
                    .iterator()
                    .next();

            return aud;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("[parseAudience] {} :{}", ErrorCode.INVALID_TOKEN, token);
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        } catch (BaseException e) {
            log.warn("[parseAudience] {} :{}", ErrorCode.EXPIRED_ACCESS_TOKEN, token);
            throw new BaseException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        }
    }

    public boolean validateToken(String accessToken) {
        log.info("validToken 진입");
        try {
            // 서명·만료 검증을 동시에 수행
            Jws<Claims> jws = jwtParser.parseSignedClaims(accessToken);

            Date expiration = jws.getPayload().getExpiration();
            log.info("validToken: {}", expiration.after(new Date()));
            log.info("expiration: {}", expiration);

            return expiration.after(new Date());
        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료", e);
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("토큰 검증 오류", e);
            throw e;
        }
    }

    public void validateTokenOrThrow(String accessToken) {
        try {
            jwtParser.parseSignedClaims(accessToken);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            throw e;
        }
    }


    public Authentication getAuthentication(String token){
        String aud = parseAudience(token); // 토큰 Aud에 Member email을 기록하고 있음
        log.info("aud:{}",aud);
        MemberDetails userDetails = memberDetailsService.loadUserByUsername(aud); // memberId를 기반으로 조회
        log.info("memberDeatils:{}",userDetails.getUsername());
        log.info("Member getAuthorities:{}",userDetails.getAuthorities());
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        log.info("getAuthentication 잘됨");
        return authentication;
    }

    public boolean validateRefreshToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);  // 만료나 서명 오류 시 예외 발생
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    public String resolvAccesseToken(HttpServletRequest request) {

        // 쿠키에서 꺼내기
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (JWT_ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    log.info("[access] cookieValue:{}",cookie.getValue());
                    return cookie.getValue();
                }
            }
        }

        // 헤더에서 꺼내기
        String bearer = request.getHeader(JWT_ACCESS_TOKEN_HEADER_NAME);
        if (StringUtils.hasText(bearer) && bearer.startsWith(JWT_ACCESS_TOKEN_TYPE)) {
            log.info("Header:{}",bearer);
            return bearer.substring(7);
        }
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {

        // 쿠키에서 꺼내기
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (JWT_REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    log.info("[refresh] cookieValue:{}",cookie.getValue());
                    return cookie.getValue();
                }
            }
        }

        // 헤더에서 꺼내기
        String bearer = request.getHeader(JWT_REFRESH_TOKEN_COOKIE_NAME);
        if (StringUtils.hasText(bearer) && bearer.startsWith(JWT_ACCESS_TOKEN_TYPE)) {
            log.info("Header:{}",bearer);
            return bearer.substring(7);
        }
        return null;
    }
}

