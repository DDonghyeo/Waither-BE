package com.waither.userservice.dto;

public record EmailVerificationDto(
        String email,
        String authCode
) {

}

