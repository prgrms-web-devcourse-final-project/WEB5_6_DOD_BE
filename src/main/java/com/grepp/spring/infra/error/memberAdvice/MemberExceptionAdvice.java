package com.grepp.spring.infra.error.memberAdvice;

import com.grepp.spring.app.controller.api.auth.payload.response.GroupAdminResponse;
import com.grepp.spring.infra.error.exceptions.member.InvalidNameException;
import com.grepp.spring.infra.error.exceptions.member.WithdrawNotAllowedException;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.MyPageErrorCode;
import com.grepp.spring.infra.response.ResponseCode;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.grepp.spring.app.controller.api")
public class MemberExceptionAdvice {

    @ExceptionHandler(WithdrawNotAllowedException.class)
    public ResponseEntity<ApiResponse<?>> withdrawNotAllowedExHandler(WithdrawNotAllowedException ex) {

        List<GroupAdminResponse> groups = ex.getLeaderGroups().stream()
            .map(group -> new GroupAdminResponse(group.getId(), group.getName()))
            .toList();

        return ResponseEntity.status(ResponseCode.NOT_ALLOWED_WITHDRAW.status())
            .body(ApiResponse.error(ResponseCode.NOT_ALLOWED_WITHDRAW, groups));
    }

    @ExceptionHandler(InvalidNameException.class)
    public ResponseEntity<ApiResponse<Void>> invalidNameExHandler(InvalidNameException ex) {
        return ResponseEntity.status(ResponseCode.BAD_REQUEST.status())
            .body(ApiResponse.error(ResponseCode.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> memberNotFoundExHandler(MemberNotFoundException ex) {
        return ResponseEntity.status(MyPageErrorCode.MEMBER_NOT_FOUND.status())
            .body(ApiResponse.error(MyPageErrorCode.MEMBER_NOT_FOUND, MyPageErrorCode.MEMBER_NOT_FOUND.message()));
    }

}
