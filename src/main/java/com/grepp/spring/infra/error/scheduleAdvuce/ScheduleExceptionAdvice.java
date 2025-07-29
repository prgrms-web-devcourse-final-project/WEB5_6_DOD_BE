package com.grepp.spring.infra.error.scheduleAdvuce;

import com.grepp.spring.infra.error.exceptions.event.EventNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.ScheduleNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.UserNotFoundException;
import com.grepp.spring.infra.error.exceptions.schedule.*;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.EventErrorCode;
import com.grepp.spring.infra.response.GroupErrorCode;
import com.grepp.spring.infra.response.ResponseCode;
import com.grepp.spring.infra.response.ScheduleErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.grepp.spring.app.controller.api.schedule")
@Slf4j
@Order(1)
public class ScheduleExceptionAdvice {

    @ExceptionHandler(VoteAlreadyProgressException.class)
    public ResponseEntity<ApiResponse<String>> notScheduleMemberExHandler(
        VoteAlreadyProgressException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ScheduleErrorCode.VOTE_ALREADY_PROGRESS));
    }

    @ExceptionHandler(NotScheduleMemberException.class)
    public ResponseEntity<ApiResponse<String>> notScheduleMemberExHandler(
        NotScheduleMemberException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(ScheduleErrorCode.NOT_SCHEDULE_MEMBER));
    }

    @ExceptionHandler(WorkSpaceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> workspaceNotFoundExHandler(
        WorkSpaceNotFoundException ex) {

        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
            .body(ApiResponse.error(ScheduleErrorCode.SCHEDULE_MEMBER_NOT_FOUND));
    }

    @ExceptionHandler(ScheduleMemberNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> scheduleMemberNotFoundExHandler(
        ScheduleMemberNotFoundException ex) {

        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
            .body(ApiResponse.error(ScheduleErrorCode.SCHEDULE_MEMBER_NOT_FOUND));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> userNotFoundExHandler(
        UserNotFoundException ex) {

        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
            .body(ApiResponse.error(GroupErrorCode.USER_NOT_FOUND));
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> locationNotFoundExHandler(
        LocationNotFoundException ex) {

        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
            .body(ApiResponse.error(GroupErrorCode.SCHEDULE_NOT_FOUND));
    }

    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> scheduleNotFoundExHandler(
        ScheduleNotFoundException ex) {

        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
            .body(ApiResponse.error(GroupErrorCode.SCHEDULE_NOT_FOUND));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> eventNotFoundExHandler(
        EventNotFoundException ex
    ) {
        return ResponseEntity.status(ResponseCode.NOT_FOUND.status()).
            body(ApiResponse.error(EventErrorCode.EVENT_NOT_FOUND));
    }

    @ExceptionHandler(NotScheduleMasterException.class)
    public ResponseEntity<ApiResponse<String>> notScheduleMasterHandler(
        NotScheduleMasterException ex
    ) {
        return ResponseEntity.status(ResponseCode.NOT_FOUND.status()).
            body(ApiResponse.error(ScheduleErrorCode.NOT_SCHEDULE_MASTER));
    }

    @ExceptionHandler(EventNotActivatedException.class)
    public ResponseEntity<ApiResponse<String>> eventNotActivatedExHandler(
        EventNotActivatedException ex
    ) {
        return ResponseEntity.status(ScheduleErrorCode.EVENT_NOT_ACTIVATED.status()).
            body(ApiResponse.error(ScheduleErrorCode.EVENT_NOT_ACTIVATED));
    }
}
