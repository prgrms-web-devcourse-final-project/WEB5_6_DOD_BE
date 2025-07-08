package com.grepp.spring.infra.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApiSpec() {
        return new OpenAPI()
            .info(new Info()
                .title("이때어때 API 문서")
                .description("이때어때 API 명세입니다. 에러 코드는 [Error Code 문서](https://www.notion.so/Error-Code-22915a0120548059a805d7c6027820d9?source=copy_link)를 참조해 주세요.")
                .version("v1.0.0"))
            .components(
                new Components()
                    .addSecuritySchemes("bearerAuth"
                        , new SecurityScheme()
                            .name("bearerAuth")
                            .type(
                                SecurityScheme.Type.HTTP)
                            .scheme(
                                "bearer")
                            .bearerFormat(
                                "JWT")
                            .description(
                                "JWT 토큰을 입력하세요. Bearer 는 생략하세요")
                    ))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        // add error type to each operation
        return (operation, handlerMethod) -> {
            operation.getResponses().addApiResponse("4xx/5xx", new ApiResponse()
                .description("Error")
                .content(new Content().addMediaType("*/*", new MediaType().schema(
                    new Schema<MediaType>().$ref("ApiErrorResponse")))));
            return operation;
        };
    }
}
