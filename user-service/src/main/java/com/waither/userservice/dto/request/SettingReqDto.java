package com.waither.userservice.dto.request;

import java.time.LocalTime;
import java.util.List;

public class SettingReqDto {

    public record CustomDto(
            boolean custom
    ) { }

    public record OutAlertDto(
            boolean outAlert
    ) { }

    public record OutAlertSetDto(
            List<String> days,
            LocalTime outTime
    ) { }

    public record ClimateAlertDto(
            boolean climateAlert
    ) { }

    public record UserAlertDto(
            boolean userAlert
    ) { }

    public record SnowAlertDto(
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
    ) {}

    public record regionReportDto(
            boolean regionReport
    ) { }

}
