package com.waither.userservice.service.commandService;

import com.waither.userservice.dto.request.SettingReqDto;
import com.waither.userservice.dto.request.SurveyReqDto;
import com.waither.userservice.entity.Region;
import com.waither.userservice.entity.Setting;
import com.waither.userservice.entity.User;
import com.waither.userservice.entity.UserData;
import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.ErrorCode;
import com.waither.userservice.repository.RegionRepository;
import com.waither.userservice.repository.SettingRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;
    private final RegionRepository regionRepository;

    // 토글 설정 변경
    public void updateBooleanSetting(User user, String settingType, Boolean value) {
        Setting setting = user.getSetting();
        switch (settingType) {
            // 사용자 맞춤 서비스 제공 설정 변경
            case "CUSTOM":
                user.setCustom(value);
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
        Setting setting = user.getSetting();

        setting.setWindAlert(windDto.windAlert());
        setting.setWindDegree(windDto.windDegree());
        settingRepository.save(setting);
    }

    // 메인 화면 날씨 상세 정보 변경
    public void updateDisplay(User user, SettingReqDto.DisplayDto displayDto) {
        Setting setting = user.getSetting();

        setting.setPrecipitation(displayDto.precipitation());
        setting.setWind(displayDto.wind());
        setting.setDust(displayDto.dust());
        settingRepository.save(setting);
    }

    // 직장 지역 설정
    public void updateRegion(User user, SettingReqDto.RegionDto regionDto) {
        Region region = user.getSetting().getRegion();
        region.update(regionDto.regionName(), regionDto.longitude(), regionDto.latitude());
        regionRepository.save(region);
    }

    // 사용자 가중치 설정
    public void updateWeight(User user, SettingReqDto.WeightDto weightDto) {
        Setting setting = user.getSetting();
        setting.setWeight(weightDto.weight());
        settingRepository.save(setting);
    }


    // 알림 설정 변경 (요일 & 시간)
    public void updateOutAlertSet(User user, SettingReqDto.OutAlertSetDto outAlertSetDto) {
        Setting setting = user.getSetting();

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
        EnumSet<DayOfWeek> selectedDays = days.stream()
                .map(day -> DayOfWeek.valueOf(day.toUpperCase()))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class)));

        setting.setSun(selectedDays.contains(DayOfWeek.SUNDAY));
        setting.setMon(selectedDays.contains(DayOfWeek.MONDAY));
        setting.setTue(selectedDays.contains(DayOfWeek.TUESDAY));
        setting.setWed(selectedDays.contains(DayOfWeek.WEDNESDAY));
        setting.setThu(selectedDays.contains(DayOfWeek.THURSDAY));
        setting.setFri(selectedDays.contains(DayOfWeek.FRIDAY));
        setting.setSat(selectedDays.contains(DayOfWeek.SATURDAY));
        settingRepository.save(setting);
    }

}
