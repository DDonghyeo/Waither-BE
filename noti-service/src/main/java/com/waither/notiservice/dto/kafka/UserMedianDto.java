package com.waither.notiservice.dto.kafka;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserMedianDto {

    public Long userId;

    public int level;

    public double temperature;

}
