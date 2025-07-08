package com.grepp.spring.infra.error;

import com.grepp.spring.infra.error.exceptions.CommonException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.grepp.spring.app.controller.api")
@Slf4j
public class RestApiExceptionAdvice {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>>
    validatorHandler(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity
                   .badRequest()
                   .body(ApiResponse.error(ResponseCode.BAD_REQUEST, errors));
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<String>>
    methodNotSupportedHandler(HttpRequestMethodNotSupportedException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                   .badRequest()
                   .body(ApiResponse.error(ResponseCode.BAD_REQUEST, ex.getMessage()));
    }
    
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ApiResponse<String>> restApiExceptionHandler(CommonException ex) {
        return ResponseEntity
                   .status(ex.code().status())
                   .body(ApiResponse.error(ex.code()));
    }
    
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<String>>  authorizationDeniedHandler(AuthorizationDeniedException ex, Model model){
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                   .status(HttpStatus.UNAUTHORIZED)
                   .body(ApiResponse.error(ResponseCode.UNAUTHORIZED));
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> runtimeExceptionHandler(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                   .internalServerError()
                   .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR));
    }
    
    
}
