package com.grepp.spring.infra.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 응답 DTO", name = "ApiResponse")
public record ApiResponse<T>(
    @Schema(description = "서비스 정의 코드", example = "2000")
    String code,
    @Schema(description = "응답 메시지", example = "성공적으로 처리되었습니다.")
    String message,
    @Schema(description = "응답 데이터")
    T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.OK.code(), ResponseCode.OK.message(), data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(ResponseCode.OK.code(), message, null);
    }

    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(ResponseCode.OK.code(), ResponseCode.OK.message(), null);
    }

    public static <T> ApiResponse<T> error(ResponseCode code) {
        return new ApiResponse<>(code.code(), code.message(), null);
    }

    public static <T> ApiResponse<T> error(ResponseCode code, String message) {
        return new ApiResponse<>(code.code(), message, null);
    }

    public static <T> ApiResponse<T> error(ResponseCode code, T data) {
        return new ApiResponse<>(code.code(), code.message(), data);
    }

    public static <T> ApiResponse<T> successCreated(T data) {
        return new ApiResponse<>(ResponseCode.CREATED.code(), ResponseCode.CREATED.message(), data);
    }

    public static <T> ApiResponse<T> conflictRegister(T data) {
        return new ApiResponse<>(ResponseCode.CONFLICT_REGISTER.code(), ResponseCode.CONFLICT_REGISTER.message(), data);
    }

    public static <T> ApiResponse<T> activateSuccess(T data) {
        return new ApiResponse<>(ResponseCode.ACTIVATED.code(), ResponseCode.ACTIVATED.message(), data);
    }
}
