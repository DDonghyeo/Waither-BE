package com.waither.userservice.dto.converter;

import com.waither.userservice.dto.response.SettingResDto;
import com.waither.userservice.entity.Setting;
import com.waither.userservice.entity.User;

import java.time.DayOfWeek;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingConverter {

    public static SettingResDto.CustomDto toCustomDto(User user) {
        return new SettingResDto.CustomDto(user.isCustom());
    }

    public static SettingResDto.RegionNameDto toRegionNameDto(Setting setting) {
        return new SettingResDto.RegionNameDto(setting.getRegion().getRegionName());
    }

    public static SettingResDto.NotificationDto toNotificationDto(Setting setting) {
        List<String> days = Stream.of(
                        new AbstractMap.SimpleEntry<>(DayOfWeek.SUNDAY, setting.isSun()),
                        new AbstractMap.SimpleEntry<>(DayOfWeek.MONDAY, setting.isMon()),
                        new AbstractMap.SimpleEntry<>(DayOfWeek.TUESDAY, setting.isTue()),
                        new AbstractMap.SimpleEntry<>(DayOfWeek.WEDNESDAY, setting.isWed()),
                        new AbstractMap.SimpleEntry<>(DayOfWeek.THURSDAY, setting.isThu()),
                        new AbstractMap.SimpleEntry<>(DayOfWeek.FRIDAY, setting.isFri()),
                        new AbstractMap.SimpleEntry<>(DayOfWeek.SATURDAY, setting.isSat())
                )
                .filter(Map.Entry::getValue)
                .map(entry -> entry.getKey().toString())
                .collect(Collectors.toList());

        return new SettingResDto.NotificationDto(
                setting.isOutAlert(),
                days,
                setting.getOutTime(),
                setting.isClimateAlert(),
                setting.isUserAlert(),
                setting.isSnowAlert()
        );
    }

    public static SettingResDto.WindDto toWindDto(Setting setting) {
        return new SettingResDto.WindDto(
                setting.isWindAlert(),
                setting.getWindDegree()
        );
    }

    public static SettingResDto.DisplayDto toDisplayDto(Setting setting) {
        return new SettingResDto.DisplayDto(
                setting.isPrecipitation(),
                setting.isWind(),
                setting.isDust()
        );
    }

    public static SettingResDto.WeightDto toWeightDto(Setting setting) {
        return new SettingResDto.WeightDto(
                setting.getWeight()
        );
    }

    public static SettingResDto.UserInfoDto toUserInfoDto(User user) {
        return new SettingResDto.UserInfoDto(
                user.getEmail(),
                user.getNickname()
        );
    }

}
