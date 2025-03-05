package com.project.backend.global.springDoc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "도서 추천 및 리뷰 서비스 API", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SpringDocConfig {
    @Bean
    public GroupedOpenApi groupBook() {
        return GroupedOpenApi.builder()
                .group("book")
                .pathsToMatch("/book/**")
                .build();
    }
    @Bean
    public GroupedOpenApi groupMember() {
        return GroupedOpenApi.builder()
                .group("member")
                .pathsToMatch("/members/**")
                .build();
    }
    @Bean
    public GroupedOpenApi groupReview() {
        return GroupedOpenApi.builder()
                .group("review")
                .pathsToMatch("/review/**")
                .build();
    }
    @Bean
    public GroupedOpenApi groupChallenge() {
        return GroupedOpenApi.builder()
                .group("challenge")
                .pathsToMatch("/challenge/**")
                .build();
    }
}
