package com.waither.notiservice.dto.kafka;

import lombok.Getter;

@Getter
public class UserSettingsDto {

    public Long userId;

    public String key;

    public String value;
}
