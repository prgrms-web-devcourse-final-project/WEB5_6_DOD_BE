package com.grepp.spring.app.model.auth.dto;

import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import com.grepp.spring.infra.auth.jwt.dto.RefreshTokenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewTokensDto {
    private AccessTokenDto newAccessToken;
    private RefreshTokenDto newRefreshToken;

}
