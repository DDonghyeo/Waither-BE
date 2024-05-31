package com.waither.notiservice.dto.kafka;

import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.utils.TemperatureUtils;
import lombok.Builder;

import java.util.List;
import java.util.Map;

public class KafkaDto {

    public record InitialDataDto(

            String email,
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
    ) {
        public UserData toUserDataEntity() {
            return UserData.builder()
                    .email(email)
                    .climateAlert(climateAlert)
                    .userAlert(userAlert)
                    .snowAlert(snowAlert)
                    .windAlert(windAlert)
                    .windDegree(windDegree)
                    .regionReport(regionReport)
                    .build();
        }

        public UserMedian toUserMedianEntity() {
            return UserMedian.builder()
                    .email(email)
                    .season(TemperatureUtils.getCurrentSeason())
                    .medianOf1And2(medianOf1And2 + weight)
                    .medianOf2And3(medianOf2And3 + weight)
                    .medianOf3And4(medianOf3And4 + weight)
                    .medianOf4And5(medianOf4And5 + weight)
                    .build();
        }
    }

    @Builder
    public record UserMedianDto(
            String email,
            List<Map<String, Double>> medians

    ) {}

    @Builder
    public record UserSettingsDto(
            String email,
            String key,
            String value
    ) {}

    @Builder
    public record TokenDto(
            String email,
            String token
    ){}

}
