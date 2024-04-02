package com.waither.userservice.accounts.dto;

import org.apache.kafka.common.protocol.types.Field;

public record LoginRequestDto(
        String email,
        String password
) {
}
