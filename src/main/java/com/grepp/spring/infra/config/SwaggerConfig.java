package com.grepp.spring.infra.config;

import com.grepp.spring.infra.auth.CurrentUser;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;


@Configuration
public class SwaggerConfig {

    @Bean
    public OperationCustomizer customizeOperation() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            // 해당 핸들러 메서드의 파라미터들을 순회
            for (java.lang.reflect.Parameter methodParameter : handlerMethod.getMethod().getParameters()) {
                // @CurrentUser 어노테이션이 붙어있는지 확인
                if (methodParameter.isAnnotationPresent(CurrentUser.class)) {
                    // 해당 파라미터를 Swagger 명세에서 제거
                    // operation.getParameters()는 null일 수 있으므로 null 체크
                    if (operation.getParameters() != null) {
                        operation.getParameters().removeIf(p -> p.getName().equals(methodParameter.getName()));
                    }
                    // 보안 요구사항 추가 (OpenAPI 3.0의 SecurityScheme 설정이 선행되어야 함)
                    operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth")); // "bearerAuth"는 SecurityScheme에 정의된 이름
                }
            }
            return operation;
        };
    }

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
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .addServersItem(new Server().url("https://ittaeok.uk"))
            .addServersItem(new Server().url("https://www.ittaeok.uk"));

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
