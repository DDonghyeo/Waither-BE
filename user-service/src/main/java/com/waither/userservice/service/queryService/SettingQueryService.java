package com.waither.userservice.service.queryService;

import com.waither.userservice.converter.SettingConverter;
import com.waither.userservice.dto.response.SettingResDto;
import com.waither.userservice.entity.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SettingQueryService {

    public SettingResDto.CustomDto getUserCustom(User user) {
        return SettingConverter.toCustomDto(user);
    }
    public SettingResDto.RegionNameDto getRegion(User user){
        return SettingConverter.toRegionNameDto(user.getSetting());
    }

    public SettingResDto.NotificationDto getNotification(User user) {
        return SettingConverter.toNotificationDto(user.getSetting());
    }

    public SettingResDto.WindDto getWind(User user) {
        return SettingConverter.toWindDto(user.getSetting());
    }

    public SettingResDto.DisplayDto getDisplay(User user) {
        return SettingConverter.toDisplayDto(user.getSetting());
    }

    public SettingResDto.WeightDto getWeight(User user) {
        return SettingConverter.toWeightDto(user.getSetting());
    }


    public SettingResDto.UserInfoDto getUserInfo(User user) {
        return SettingConverter.toUserInfoDto(user);
    }

}
