package com.waither.userservice.dto.response;

import java.time.LocalTime;
import java.util.List;

public class SettingResDto {

    public record CustomDto(
            boolean custom
    ) { }

    public record RegionNameDto(
            String regionName
    ) { }

    public record NotificationDto(
            boolean outAlert,
            List<String> days,
            LocalTime outTime,
            boolean climateAlert,
            boolean userAlert,
            boolean snowAlert
    ) { }

    public record WindDto(
            boolean windAlert,
            Integer windDegree
    ) { }

    public record DisplayDto(
            boolean precipitation,
            boolean wind,
            boolean dust
    ) { }

    public record WeightDto(
            Double weight
    ) { }

    public record UserInfoDto(
            String email,
            String nickname
    ) { }

}
