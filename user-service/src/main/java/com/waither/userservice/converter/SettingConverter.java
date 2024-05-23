package com.waither.userservice.converter;

import com.waither.userservice.dto.response.SettingResDto;
import com.waither.userservice.entity.Region;
import com.waither.userservice.entity.Setting;
import com.waither.userservice.entity.User;
import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class SettingConverter {

    // Setting을 기본값으로 설정
    public static Setting createSetting() {
        // Setting을 기본값으로 설정
        return Setting.builder()
                .climateAlert(true)
                .userAlert(true)
                .snowAlert(true)
                .windAlert(true)
                .windDegree(10)
                .regionReport(true)
                .precipitation(true)
                .wind(true)
                .dust(true)
                .weight(0.0)
                .build();
    }

    public static SettingResDto.CustomDto toCustomDto(User user) {
        return new SettingResDto.CustomDto(user.isCustom());
    }

    public static SettingResDto.RegionNameDto toRegionNameDto(Setting setting) {
        return new SettingResDto.RegionNameDto(setting.getRegion().getRegionName());
    }

    public static SettingResDto.NotificationDto toNotificationDto(Setting setting) {
        List<String> days = setting.getDays().stream()
                .map(Enum::toString)
                .toList();

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
        return new SettingResDto.WeightDto(setting.getWeight());
    }

    public static SettingResDto.UserInfoDto toUserInfoDto(User user) {
        return new SettingResDto.UserInfoDto(
                user.getEmail(),
                user.getNickname()
        );
    }

}
