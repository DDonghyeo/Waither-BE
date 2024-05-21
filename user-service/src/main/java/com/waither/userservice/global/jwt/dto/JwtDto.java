package com.waither.userservice.global.jwt.dto;

public record JwtDto(
        String accessToken,
        String refreshToken
) {
}