package com.grepp.spring.infra.error.mypageAdvice;

import com.grepp.spring.infra.error.exceptions.mypage.AuthenticationRequiredException;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteAlreadyExistException;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteNotFoundException;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteSaveFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.GoogleCalendarApiFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidFavoriteRequestException;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidMemberRequestException;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidPublicCalendarIdException;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {
    "com.grepp.spring.app.controller.api.mypage",
    "com.grepp.spring.app.controller.api.mainpage"
})
@Slf4j
@Order(1)
public class MyPageExceptionAdvice {

  // 400
  @ExceptionHandler(InvalidFavoriteRequestException.class)
  public ResponseEntity<ApiResponse<String>> invalidFavoriteRequestExHandler(
      FavoriteNotFoundException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  @ExceptionHandler(InvalidMemberRequestException.class)
  public ResponseEntity<ApiResponse<String>> invalidMemberRequestExHandler(
      InvalidMemberRequestException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  @ExceptionHandler(InvalidPublicCalendarIdException.class)
  public ResponseEntity<ApiResponse<String>> invalidPublicCalendarIdExHandler(
      InvalidPublicCalendarIdException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  // 401
  @ExceptionHandler(AuthenticationRequiredException.class) // 멤버 인증 관련
  public ResponseEntity<ApiResponse<String>> authenticationRequiredExHandler(
      AuthenticationRequiredException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  // 404
  @ExceptionHandler(FavoriteNotFoundException.class)
  public ResponseEntity<ApiResponse<String>> favoriteNotFoundExHandlerExHandler(
      FavoriteNotFoundException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  @ExceptionHandler(MemberNotFoundException.class) // 멤버 관련
  public ResponseEntity<ApiResponse<String>> memberNotFoundExHandlerExHandler(
      MemberNotFoundException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  // 409
  @ExceptionHandler(FavoriteAlreadyExistException.class)
  public ResponseEntity<ApiResponse<String>> favoriteAlreadyExistExHandler(
      FavoriteAlreadyExistException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  // 500
  @ExceptionHandler(FavoriteSaveFailedException.class)
  public ResponseEntity<ApiResponse<String>> FavoriteSaveFailedExHandler(
      FavoriteSaveFailedException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  // 503
  @ExceptionHandler(GoogleCalendarApiFailedException.class)
  public ResponseEntity<ApiResponse<String>> GoogleCalendarApiExHandler(
      GoogleCalendarApiFailedException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }
}
