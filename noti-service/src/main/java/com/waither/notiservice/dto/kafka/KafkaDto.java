package com.waither.notiservice.dto.kafka;

import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.utils.TemperatureUtils;
import lombok.Builder;

import java.util.List;
import java.util.Map;

public class KafkaDto {

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
    ) {
        public UserData toUserDataEntity() {
            return UserData.builder()
                    .userId(0L)
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
                    .season(TemperatureUtils.getCurrentSeason())
                    .medianOf1And2(medianOf1And2 + weight)
                    .medianOf2And3(medianOf2And3 + weight)
                    .medianOf3And4(medianOf3And4 + weight)
                    .medianOf4And5(medianOf4And5 + weight)
                    .build();
        }
    }

    public record UserMedianDto(
            Long userId,
            List<Map<String, Double>> medians

    ) {}

    public record UserSettingsDto(
            Long userId,
            String key,
            String value
    ) {}

    public record TokenDto(
            Long userId,
            String token
    ){}

}
