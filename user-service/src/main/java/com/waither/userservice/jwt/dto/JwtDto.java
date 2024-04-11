package com.waither.userservice.jwt.dto;

public record JwtDto(
        String accessToken,
        String refreshToken
) {
}