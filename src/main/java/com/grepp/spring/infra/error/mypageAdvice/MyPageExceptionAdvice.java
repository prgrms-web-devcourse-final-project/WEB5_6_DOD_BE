package com.grepp.spring.infra.error.mypageAdvice;

import com.grepp.spring.infra.error.exceptions.mypage.AuthenticationRequiredException;
import com.grepp.spring.infra.error.exceptions.mypage.CalendarAuthRequiredException;
import com.grepp.spring.infra.error.exceptions.mypage.CalendarEventSaveFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.CalendarSyncFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.CalendarTokenExpiredException;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteAlreadyExistException;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteNotFoundException;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteSaveFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.GoogleAuthFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidCalendarResponseException;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidFavoriteRequestException;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidMemberRequestException;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.error.exceptions.mypage.TokenSaveFailedException;
import com.grepp.spring.infra.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.grepp.spring.app.controller.api.mypage")
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
  // 401
  @ExceptionHandler(AuthenticationRequiredException.class) // 멤버 인증 관련
  public ResponseEntity<ApiResponse<String>> authenticationRequiredExHandler(
      AuthenticationRequiredException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  @ExceptionHandler(GoogleAuthFailedException.class)
  public ResponseEntity<ApiResponse<String>> googleAuthFailedExHandler(
      GoogleAuthFailedException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  @ExceptionHandler(CalendarAuthRequiredException.class)
  public ResponseEntity<ApiResponse<String>> handleCalendarAuthRequiredExHandler(
      CalendarAuthRequiredException ex) {

    return ResponseEntity
        .status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode(), ex.getRedirectUrl()));
  }

  @ExceptionHandler(CalendarTokenExpiredException.class)
  public ResponseEntity<ApiResponse<String>> handleCalendarTokenExpiredExHandler(
      CalendarTokenExpiredException ex) {
    return ResponseEntity
        .status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode(), ex.getRedirectUrl()));
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
  @ExceptionHandler(CalendarSyncFailedException.class)
  public ResponseEntity<ApiResponse<String>> calendarSyncFailedExHandler(
      CalendarSyncFailedException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  @ExceptionHandler(InvalidCalendarResponseException.class)
  public ResponseEntity<ApiResponse<String>> invalidCalendarResponseExHandler(
      InvalidCalendarResponseException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  @ExceptionHandler(CalendarEventSaveFailedException.class)
  public ResponseEntity<ApiResponse<String>> calendarEventSaveFailedExHandler(
      CalendarEventSaveFailedException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  @ExceptionHandler(TokenSaveFailedException.class)
  public ResponseEntity<ApiResponse<String>> TokenSaveFailedExHandler(
      TokenSaveFailedException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }

  @ExceptionHandler(FavoriteSaveFailedException.class)
  public ResponseEntity<ApiResponse<String>> FavoriteSaveFailedExHandler(
      FavoriteSaveFailedException ex) {

    return ResponseEntity.status(ex.getCode().status())
        .body(ApiResponse.error(ex.getCode()));
  }
}
