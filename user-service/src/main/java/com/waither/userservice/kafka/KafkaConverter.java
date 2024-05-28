package com.waither.userservice.kafka;

import com.waither.userservice.entity.UserMedian;
import com.waither.userservice.entity.Setting;
import com.waither.userservice.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaConverter {

    public static KafkaDto.InitialDataDto toInitialData(User user, Setting setting, UserMedian userMedian
    ) {
        return KafkaDto.InitialDataDto.builder()
                .nickName(user.getNickname())
                .climateAlert(setting.isClimateAlert())
                .userAlert(setting.isUserAlert())
                .snowAlert(setting.isSnowAlert())
                .windAlert(setting.isWindAlert())
                .windDegree(setting.getWindDegree())
                .regionReport(setting.isRegionReport())
                .weight(setting.getWeight())
                .medianOf1And2(userMedian.getMedianOf1And2())
                .medianOf2And3(userMedian.getMedianOf2And3())
                .medianOf3And4(userMedian.getMedianOf3And4())
                .medianOf4And5(userMedian.getMedianOf4And5())
                .build();
    }

    public static KafkaDto.UserMedianDto toUserMedianDto(User user, UserMedian userMedian) {
        List<Map<String, Double>> medians = Arrays.asList(
                Map.of("medianOf1And2", userMedian.getMedianOf1And2()),
                Map.of("medianOf2And3", userMedian.getMedianOf2And3()),
                Map.of("medianOf3And4", userMedian.getMedianOf3And4()),
                Map.of("medianOf4And5", userMedian.getMedianOf4And5())
        );
        return new KafkaDto.UserMedianDto(user.getId(), medians);
    }

    public static KafkaDto.UserSettingsDto toSettingDto(User user, String key, String value) {
        return new KafkaDto.UserSettingsDto(user.getId(), key, value);
    }

}
