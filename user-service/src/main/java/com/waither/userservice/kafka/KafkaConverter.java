package com.waither.userservice.kafka;

import com.waither.userservice.entity.UserMedian;
import com.waither.userservice.entity.Setting;
import com.waither.userservice.entity.User;
import com.waither.userservice.entity.enums.Season;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaConverter {

    public static KafkaDto.InitialDataDto toInitialData(User user, Setting setting, List<UserMedian> userMedians) {
        Map<Season, UserMedian> userMedianMap = userMedians.stream()
                .collect(Collectors.toMap(UserMedian::getSeason, Function.identity()));

        Map<Season, KafkaDto.SeasonData> seasonDataMap = userMedianMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> KafkaDto.SeasonData.builder()
                                .medianOf1And2(entry.getValue().getMedianOf1And2())
                                .medianOf2And3(entry.getValue().getMedianOf2And3())
                                .medianOf3And4(entry.getValue().getMedianOf3And4())
                                .medianOf4And5(entry.getValue().getMedianOf4And5())
                                .build(),
                        (a, b) -> {
                            throw new IllegalStateException("Duplicate key");
                        },
                        () -> new EnumMap<>(Season.class)
                ));

        return KafkaDto.InitialDataDto.builder()
                .nickName(user.getNickname())
                .climateAlert(setting.isClimateAlert())
                .userAlert(setting.isUserAlert())
                .snowAlert(setting.isSnowAlert())
                .windAlert(setting.isWindAlert())
                .windDegree(setting.getWindDegree())
                .regionReport(setting.isRegionReport())
                .weight(setting.getWeight())
                .seasonData(seasonDataMap)
                .build();
    }

    public static KafkaDto.UserMedianDto toUserMedianDto(User user, UserMedian userMedian) {
        KafkaDto.SeasonData seasonData = KafkaDto.SeasonData.builder()
                .medianOf1And2(userMedian.getMedianOf1And2())
                .medianOf2And3(userMedian.getMedianOf2And3())
                .medianOf3And4(userMedian.getMedianOf3And4())
                .medianOf4And5(userMedian.getMedianOf4And5())
                .build();

        return KafkaDto.UserMedianDto.builder()
                .email(user.getEmail())
                .seasonData(seasonData)
                .build();
    }

    public static KafkaDto.UserSettingsDto toSettingDto(User user, String key, String value) {
        return KafkaDto.UserSettingsDto.builder()
                .email(user.getEmail())
                .key(key)
                .value(value)
                .build();
    }

}
