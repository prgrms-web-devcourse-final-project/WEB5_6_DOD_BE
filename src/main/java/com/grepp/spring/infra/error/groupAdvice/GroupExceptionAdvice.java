package com.grepp.spring.infra.error.groupAdvice;


import com.grepp.spring.infra.error.exceptions.group.GroupNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupUserException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.GroupErrorCode;
import com.grepp.spring.infra.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice (basePackages = "com.grepp.spring.app.controller.api")
@Slf4j
public class GroupExceptionAdvice {

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> groupNotFoundExHandler(
        GroupNotFoundException ex){

        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
            .body(ApiResponse.error(GroupErrorCode.GROUP_NOT_FOUND));
    }


    @ExceptionHandler(NotGroupUserException.class)
    public ResponseEntity<ApiResponse<String>> notGroupUserExHandler(
        NotGroupUserException ex){

        return ResponseEntity.status(ResponseCode.NOT_FOUND.status())
            .body(ApiResponse.error(GroupErrorCode.NOT_GROUP_MEMBER));
    }

}
