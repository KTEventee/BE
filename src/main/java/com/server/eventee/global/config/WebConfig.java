package com.server.eventee.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class WebConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);     // IP, 세션ID, 사용자 이름
        filter.setIncludeQueryString(true);    // ?a=1&b=2
        filter.setIncludePayload(true);        // POST/PUT 페이로드
        filter.setMaxPayloadLength(10000);     // 최대 바디 길이 (byte)
        filter.setIncludeHeaders(false);       // true로 하면 헤더도 찍음
        return filter;
    }
}