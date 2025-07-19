package com.grepp.spring.infra.error.scheduleAdvuce;


import com.grepp.spring.infra.error.exceptions.group.ScheduleNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.UserNotFoundException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.GroupErrorCode;
import com.grepp.spring.infra.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.grepp.spring.app.controller.api.schedule")
@Slf4j
@Order(1)
public class ScheduleExceptionAdvice {

//    //403
//    @ExceptionHandler(NotGroupLeaderException.class)
//    public ResponseEntity<ApiResponse<String>> notGroupLeaderExHandler(
//        NotGroupLeaderException ex) {
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//            .body(ApiResponse.error(GroupErrorCode.NOT_GROUP_LEADER));
//    }
//
//    @ExceptionHandler(NotGroupUserException.class)
//    public ResponseEntity<ApiResponse<String>> notGroupUserExHandler(
//        NotGroupUserException ex) {
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//            .body(ApiResponse.error(GroupErrorCode.NOT_GROUP_MEMBER));
//    }
//
//    @ExceptionHandler(NotScheduleLeaderException.class)
//    public ResponseEntity<ApiResponse<String>> notScheduleLeaderExHandler(
//        NotScheduleLeaderException ex) {
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//            .body(ApiResponse.error(GroupErrorCode.NOT_SCHEDULE_LEADER));
//    }
//
//    @ExceptionHandler(NotScheduleUserException.class)
//    public ResponseEntity<ApiResponse<String>> notScheduleUserExHandler(
//        NotScheduleUserException ex) {
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//            .body(ApiResponse.error(GroupErrorCode.NOT_SCHEDULE_MEMBER));
//    }
//
//
//    // 404
//    @ExceptionHandler(GroupNotFoundException.class)
//    public ResponseEntity<ApiResponse<String>> groupNotFoundExHandler(
//        GroupNotFoundException ex) {
//        log.error("404예외");
//
//        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
//            .body(ApiResponse.error(GroupErrorCode.GROUP_NOT_FOUND));
//    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> userNotFoundExHandler(
        UserNotFoundException ex) {

        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
            .body(ApiResponse.error(GroupErrorCode.USER_NOT_FOUND));
    }

    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> scheduleNotFoundExHandler(
        ScheduleNotFoundException ex) {

        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
            .body(ApiResponse.error(GroupErrorCode.SCHEDULE_NOT_FOUND));
    }
//
//    @ExceptionHandler(UserNotInGroupException.class)
//    public ResponseEntity<ApiResponse<String>> userNotInGroupExHandler(
//        UserNotInGroupException ex) {
//
//        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
//            .body(ApiResponse.error(GroupErrorCode.USER_NOT_IN_GROUP));
//    }
//
//
//    // 409
//    @ExceptionHandler(UserAlreadyInGroupException.class)
//    public ResponseEntity<ApiResponse<String>> userAlreadyExHandler(
//        UserAlreadyInGroupException ex) {
//
//        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
//            .body(ApiResponse.error(GroupErrorCode.USER_ALREADY_IN_GROUP));
//    }
//
//    @ExceptionHandler(ScheduleAlreadyInGroupException.class)
//    public ResponseEntity<ApiResponse<String>> scheduleAlreadyExHandler(
//        ScheduleAlreadyInGroupException ex) {
//
//        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
//            .body(ApiResponse.error(GroupErrorCode.SCHEDULE_ALREADY_IN_GROUP));
//    }
//
//    @ExceptionHandler(OnlyOneGroupLeaderException.class)
//    public ResponseEntity<ApiResponse<String>> onlyOneGroupLeaderExHandler(
//        OnlyOneGroupLeaderException ex) {
//
//        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
//            .body(ApiResponse.error(GroupErrorCode.ONE_GROUP_LEADER));
//    }
//
//    @ExceptionHandler(UserGroupLeaderException.class)
//    public ResponseEntity<ApiResponse<String>> userIsGroupLeaderExHandler(
//        UserGroupLeaderException ex) {
//
//        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
//            .body(ApiResponse.error(GroupErrorCode.USER_GROUP_LEADER));
//    }
}
