package com.waither.userservice.kafka;

import lombok.Builder;

import java.util.List;
import java.util.Map;

public class KafkaDto {

    @Builder
    public record InitialDataDto(

            String nickName,
            boolean climateAlert,
            boolean userAlert,
            boolean snowAlert,
            boolean windAlert,
            Integer windDegree,
            boolean regionReport,
            Double weight,
            Double medianOf1And2,
            Double medianOf2And3,
            Double medianOf3And4,
            Double medianOf4And5
    ) {}

    public record UserMedianDto(
            Long userId,
            List<Map<String, Double>> medians

    ) {}

    public record UserSettingsDto(
            Long userId,
            String key,
            String value
    ) {}

}
