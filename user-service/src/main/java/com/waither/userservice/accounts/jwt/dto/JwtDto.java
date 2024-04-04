package com.waither.userservice.accounts.jwt.dto;

public record JwtDto(
        String accessToken,
        String refreshToken
) {
}