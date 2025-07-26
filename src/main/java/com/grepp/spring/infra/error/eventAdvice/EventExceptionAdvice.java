package com.grepp.spring.infra.error.eventAdvice;

import com.grepp.spring.infra.error.exceptions.event.*;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.EventErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.grepp.spring.app.controller.api.event")
@Slf4j
@Order(1)
public class EventExceptionAdvice {

    @ExceptionHandler(InvalidEventDataException.class)
    public ResponseEntity<ApiResponse<String>> invalidEventDataExHandler(
        InvalidEventDataException ex) {

        return ResponseEntity.status(ex.code().status())
            .body(ApiResponse.error(ex.code()));
    }

    @ExceptionHandler(AlreadyConfirmedScheduleException.class)
    public ResponseEntity<ApiResponse<String>> alreadyCompletedScheduleExHandler(
        AlreadyConfirmedScheduleException ex) {

        return ResponseEntity.status(ex.code().status())
            .body(ApiResponse.error(ex.code()));
    }

    @ExceptionHandler(EventAlreadyCompletedException.class)
    public ResponseEntity<ApiResponse<String>> eventAlreadyCompletedExHandler(
        EventAlreadyCompletedException ex) {

        return ResponseEntity.status(ex.code().status())
            .body(ApiResponse.error(ex.code()));
    }

    @ExceptionHandler(EventAuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> eventAuthenticationExHandler(
        EventAuthenticationException ex) {

        return ResponseEntity.status(ex.code().status())
            .body(ApiResponse.error(ex.code()));
    }

    @ExceptionHandler(NotEventMemberException.class)
    public ResponseEntity<ApiResponse<String>> notEventMemberExHandler(
        NotEventMemberException ex) {

        return ResponseEntity.status(ex.code().status())
            .body(ApiResponse.error(ex.code()));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> eventNotFoundExHandler(
        EventNotFoundException ex) {

        return ResponseEntity.status(EventErrorCode.EVENT_NOT_FOUND.status())
            .body(ApiResponse.error(ex.code()));
    }

    @ExceptionHandler(ScheduleResultNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> scheduleResultNotFoundExHandler(
        ScheduleResultNotFoundException ex) {

        return ResponseEntity.status(ex.code().status())
            .body(ApiResponse.error(ex.code()));
    }

    @ExceptionHandler(AlreadyJoinedEventException.class)
    public ResponseEntity<ApiResponse<String>> alreadyJoinedEventExHandler(
        AlreadyJoinedEventException ex) {

        return ResponseEntity.status(ex.code().status())
            .body(ApiResponse.error(ex.code()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Map<String, String>>> validationExHandler(
        BindException ex) {

        return ResponseEntity.status(EventErrorCode.INVALID_EVENT_DATA.status())
            .body(ApiResponse.error(EventErrorCode.INVALID_EVENT_DATA));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> illegalArgumentExHandler(
        IllegalArgumentException ex) {

        return ResponseEntity.status(EventErrorCode.INVALID_EVENT_DATA.status())
            .body(ApiResponse.error(EventErrorCode.INVALID_EVENT_DATA));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> illegalStateExHandler(
        IllegalStateException ex) {

        return ResponseEntity.status(EventErrorCode.INVALID_EVENT_DATA.status())
            .body(ApiResponse.error(EventErrorCode.INVALID_EVENT_DATA));
    }
}