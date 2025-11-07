package com.server.eventee.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "JWT",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@SecurityRequirement(name = "JWT")
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("JWT")) // ✅ 이 부분이 핵심
            .info(new Info()
                .title("Eventee API")
                .description("이벤티 API")
                .version("v1.0.0"));
    }
}
