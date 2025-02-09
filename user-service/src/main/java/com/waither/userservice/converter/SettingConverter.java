package com.waither.userservice.converter;

import com.waither.userservice.dto.response.SettingResDto;
import com.waither.userservice.entity.Setting;
import com.waither.userservice.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SettingConverter {

    public static SettingResDto.CustomDto toCustomDto(User user) {
        return SettingResDto.CustomDto.builder()
                .custom(user.isCustom())
                .build();
    }

    public static SettingResDto.RegionNameDto toRegionNameDto(Setting setting) {
        return SettingResDto.RegionNameDto.builder()
                .regionName(setting.getRegion().getRegionName())
                .regionReport(setting.isRegionReport())
                .build();
    }

    public static SettingResDto.NotificationDto toNotificationDto(Setting setting) {
        List<String> days = setting.getDays().stream()
                .map(Enum::toString)
                .toList();

        return SettingResDto.NotificationDto.builder()
                .outAlert(setting.isOutAlert())
                .days(days)
                .outTime(setting.getOutTime())
                .climateAlert(setting.isClimateAlert())
                .userAlert(setting.isUserAlert())
                .snowAlert(setting.isSnowAlert())
                .build();
    }

    public static SettingResDto.WindDto toWindDto(Setting setting) {
        return SettingResDto.WindDto.builder()
                .windAlert(setting.isWindAlert())
                .windDegree(setting.getWindDegree())
                .build();
    }

    public static SettingResDto.DisplayDto toDisplayDto(Setting setting) {
        return SettingResDto.DisplayDto.builder()
                .precipitation(setting.isPrecipitation())
                .wind(setting.isWind())
                .dust(setting.isDust())
                .build();
    }

    public static SettingResDto.WeightDto toWeightDto(Setting setting) {
        return SettingResDto.WeightDto.builder()
                .weight(setting.getWeight())
                .build();
    }

    public static SettingResDto.UserInfoDto toUserInfoDto(User user) {
        return SettingResDto.UserInfoDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}