package com.waither.userservice.controller;

import com.waither.userservice.dto.request.SettingReqDto;
import com.waither.userservice.entity.User;
import com.waither.userservice.global.annotation.AuthUser;
import com.waither.userservice.global.response.ApiResponse;
import com.waither.userservice.service.SettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/setting")
public class SettingController {

    private final SettingService settingService;

    // 사용자 맞춤 서비스 제공
    @PatchMapping("/custom")
    public ApiResponse<String> customUpdate(@AuthUser User user, @RequestBody SettingReqDto.CustomDto customDto) {
        settingService.updateBooleanSetting(user, "CUSTOM", customDto.custom());
        return ApiResponse.onSuccess("사용자 맞춤 서비스 제공이 " + customDto.custom() + "로 바뀌었습니다.");
    }

    // 외출 알림 설정
    @PatchMapping("/noti/out-alert")
    public ApiResponse<String> outAlertUpdate(@AuthUser User user, @RequestBody SettingReqDto.OutAlertDto outAlertDto) {
        settingService.updateBooleanSetting(user, "OUT_ALERT" , outAlertDto.outAlert());
        return ApiResponse.onSuccess("외출 알림 설정이 " + outAlertDto.outAlert() + "로 바뀌었습니다.");
    }

    // 알림 설정 (요일 & 시간)
    @PatchMapping("/noti/out-alert-set")
    public ApiResponse<String> outAlertSetUpdate(@AuthUser User user, @RequestBody SettingReqDto.OutAlertSetDto outAlertSetDto) {
        settingService.updateOutAlertSet(user, outAlertSetDto);
        return ApiResponse.onSuccess(
                outAlertSetDto.days() + "요일, "
                        + outAlertSetDto.outTime() + "시로 알림 설정이 바뀌었습니다.");
    }

    // 기상 특보 알림 설정
    @PatchMapping("/noti/climate-alert")
    public ApiResponse<String> climateAlertUpdate(@AuthUser User user, @RequestBody SettingReqDto.ClimateAlertDto climateAlertDto) {
        settingService.updateBooleanSetting(user, "CUSTOM", climateAlertDto.climateAlert());
        return ApiResponse.onSuccess("기상 특보 알림 설정이 " + climateAlertDto.climateAlert() + "로 바뀌었습니다.");
    }

    // 사용자 맞춤 예보 설정
    @PatchMapping("/noti/user-alert")
    public ApiResponse<String> userAlertUpdate(@AuthUser User user, @RequestBody SettingReqDto.UserAlertDto userAlertDto) {
        settingService.updateBooleanSetting(user, "USER_ALERT", userAlertDto.userAlert());
        return ApiResponse.onSuccess("사용자 맞춤 예보 설정이 " + userAlertDto.userAlert() + "로 바뀌었습니다.");
    }

    // 강설 정보 알림 설정
    @PatchMapping("/noti/snow-alert")
    public ApiResponse<String> snowAlertUpdate(@AuthUser User user, @RequestBody SettingReqDto.SnowAlertDto snowAlertDto) {
        settingService.updateBooleanSetting(user, "SNOW_ALERT", snowAlertDto.snowAlert());
        return ApiResponse.onSuccess("강설 정보 알림 설정이 " + snowAlertDto.snowAlert() + "로 바뀌었습니다.");
    }

    // 바람 세기 알림
    @PatchMapping("/noti/wind")
    public ApiResponse<String> windUpdate(@AuthUser User user, @RequestBody SettingReqDto.WindDto windDto) {
        settingService.updateWind(user, windDto);
        return ApiResponse.onSuccess(
                "바람 세기 알림 설정이 " + windDto.windAlert() + "로 바뀌었습니다. "
                        + "강도는 " + windDto.windDegree() + " m/s 이상 일 때 알림이 발송 됩니다.");
    }

    // 메인 화면 날씨 상세 정보 보기 (강수량, 퓽향/풍속, 미세먼지)
    @PatchMapping("/display")
    public ApiResponse<String> displayUpdate(@AuthUser User user, @RequestBody SettingReqDto.DisplayDto displayDto) {
        settingService.updateDisplay(user, displayDto);
        return ApiResponse.onSuccess(
                "강수량 보기는 " + displayDto.precipitation() + "로, "
                        + "풍향/풍속 보기는 " + displayDto.wind() + "로, "
                        + "미세먼지 보기는 " + displayDto.dust() + "로 "
                        + "설정되었습니다.");
    }

    // 직장 지역 레포트 알림 받기
    @PatchMapping("/region-report")
    public ApiResponse<String> regionReportUpdate(@AuthUser User user, @RequestBody SettingReqDto.regionReportDto regionReportDto) {
        settingService.updateBooleanSetting(user, "REGION_REPORT", regionReportDto.regionReport());
        return ApiResponse.onSuccess("직장 지역 레포트 알림 설정이 " + regionReportDto.regionReport() + "로 바뀌었습니다.");
    }




}
