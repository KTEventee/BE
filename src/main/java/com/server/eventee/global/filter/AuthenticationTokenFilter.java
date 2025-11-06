package com.server.eventee.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.eventee.domain.member.service.MemberDetailsService;
import com.server.eventee.global.exception.BaseResponse;
import com.server.eventee.global.exception.codes.ErrorCode;
import com.server.eventee.global.token.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        log.info("진입 path:{}",path);

        //note 무시하는 endpoint들
        final String[] IGNORE_PATHS = {
                "/css", "/js", "/plugins"
        };

        for (String allowed : IGNORE_PATHS) {
            if (path.startsWith(allowed)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (path.startsWith("/member/login")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                response.sendRedirect("/home"); // 이미 로그인된 사용자면 홈으로
                return;
            }
            filterChain.doFilter(request, response); // 로그인 안됐으면 그대로 통과 (로그인 페이지 표시)
            return;
        }

        log.info(request.getRequestURI());
        log.info("멤버 인증 시작!!");
        String token = jwtProvider.resolvAccesseToken(request);
        log.info("최종 token:{}",token);
        if(token!=null) {
            try {
                jwtProvider.validateTokenOrThrow(token);

                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return ;
            } catch (ExpiredJwtException e) {
                log.warn(e.getMessage());
                log.warn("authentication에서 오류발생");
                request.setAttribute("tokenExpired", true); // 포워드 시 전달용
            }
        }
        SecurityContextHolder.clearContext();
        filterChain.doFilter(request, response);
    }
}
