package com.waither.notiservice.dto.kafka;

import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.enums.Season;
import com.waither.notiservice.utils.TemperatureUtils;
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
            Map<String, SeasonData> seasonData
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

        public List<UserMedian> toUserMedianList() {
            return seasonData.entrySet().stream()
                    .map(entry -> UserMedian.builder()
                            .email(email)
                            .season(Season.valueOf(entry.getKey()))
                            .medianOf1And2(entry.getValue().medianOf1And2())
                            .medianOf2And3(entry.getValue().medianOf2And3())
                            .medianOf3And4(entry.getValue().medianOf3And4())
                            .medianOf4And5(entry.getValue().medianOf4And5())
                            .build())
                    .toList();
        }
    }

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

    @Builder
    public record TokenDto(
            String email,
            String token
    ){}

}
