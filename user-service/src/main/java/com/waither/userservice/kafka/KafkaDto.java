package com.waither.userservice.kafka;

import com.waither.userservice.entity.enums.Season;
import lombok.Builder;

import java.util.List;
import java.util.Map;

public class KafkaDto {

    @Builder
    public record SeasonData(
            Double medianOf1And2,
            Double medianOf2And3,
            Double medianOf3And4,
            Double medianOf4And5
    ) {}

    @Builder
    public record InitialDataDto(
            String email,
            String nickName,
            boolean climateAlert,
            boolean userAlert,
            boolean snowAlert,
            boolean windAlert,
            int windDegree,
            boolean regionReport,
            double weight,
            Map<Season, SeasonData> seasonData
    ) {}

    @Builder
    public record UserMedianDto(
            String email,
            SeasonData seasonData
    ) {}

    @Builder
    public record UserSettingsDto(
            String email,
            String key,
            String value
    ) {}

}
