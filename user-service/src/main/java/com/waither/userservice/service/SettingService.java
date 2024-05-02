package com.waither.userservice.service;

import com.waither.userservice.dto.request.SettingReqDto;
import com.waither.userservice.entity.Setting;
import com.waither.userservice.entity.User;
import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.ErrorCode;
import com.waither.userservice.repository.SettingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;

    public Setting getUserSetting(User user) {
        Optional<Setting> settingEntity = settingRepository.findByUser(user);
        if (settingEntity.isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }
        return settingEntity.get();
    }

    // 토글 설정 변경
    public void updateBooleanSetting(User user, String settingType, Boolean value) {
        Setting setting = getUserSetting(user);
        switch (settingType) {
            // 사용자 맞춤 서비스 제공 설정 변경
            case "CUSTOM":
                setting.setCustom(value);
                break;
            // 외출 알림 설정 변경
            case "OUT_ALERT":
                setting.setOutAlert(value);
                break;
            // 기상 특보 알림 설정 변경
            case "CLIMATE_ALERT":
                setting.setClimateAlert(value);
                break;
            // 사용자 맞춤 예보 설정 변경
            case "USER_ALERT":
                setting.setUserAlert(value);
                break;
            // 강설 정보 알림 설정 변경
            case "SNOW_ALERT":
                setting.setSnowAlert(value);
                break;
            // 직장 지역 레포트 알림 받기
            case "REGION_REPORT":
                setting.setRegionReport(value);
                break;
            default:
                throw new CustomException(ErrorCode.UNSUPPORTED_SETTING_TYPE);
        }
        settingRepository.save(setting);
    }

    // 바람 세기 알림 설정 변경
    public void updateWind(User user, SettingReqDto.WindDto windDto) {
        Setting setting = getUserSetting(user);

        setting.setWindAlert(windDto.windAlert());
        setting.setWindDegree(windDto.windDegree());
        settingRepository.save(setting);
    }

    // 메인 화면 날씨 상세 정보 변경
    public void updateDisplay(User user, SettingReqDto.DisplayDto displayDto) {
        Setting setting = getUserSetting(user);

        setting.setPrecipitation(displayDto.precipitation());
        setting.setWind(displayDto.wind());
        setting.setDust(displayDto.dust());
        settingRepository.save(setting);
    }

    // 알림 설정 변경 (요일 & 시간)
    public void updateOutAlertSet(User user, SettingReqDto.OutAlertSetDto outAlertSetDto) {
        Setting setting = getUserSetting(user);

        // 요일 설정
        updateDays(setting, outAlertSetDto.days());

        // 외출 시간 설정
        if (outAlertSetDto.outTime() != null) {
            setting.setOutTime(outAlertSetDto.outTime());
        }
        settingRepository.save(setting);
    }

    // 요일 설정 업데이트
    private void updateDays(Setting setting, List<String> days) {
        boolean[] dayOfWeekFlags = new boolean[7]; // 요일 개수에 해당하는 배열 생성

        // 선택된 요일만 true로 설정
        days.forEach(day -> {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
            dayOfWeekFlags[dayOfWeek.ordinal()] = true;
        });

        // 각 요일에 대해 해당하는 필드를 설정
        setting.setSun(dayOfWeekFlags[DayOfWeek.SUNDAY.ordinal()]);
        setting.setMon(dayOfWeekFlags[DayOfWeek.MONDAY.ordinal()]);
        setting.setTue(dayOfWeekFlags[DayOfWeek.TUESDAY.ordinal()]);
        setting.setWed(dayOfWeekFlags[DayOfWeek.WEDNESDAY.ordinal()]);
        setting.setThu(dayOfWeekFlags[DayOfWeek.THURSDAY.ordinal()]);
        setting.setFri(dayOfWeekFlags[DayOfWeek.FRIDAY.ordinal()]);
        setting.setSat(dayOfWeekFlags[DayOfWeek.SATURDAY.ordinal()]);
        settingRepository.save(setting);
    }

}
