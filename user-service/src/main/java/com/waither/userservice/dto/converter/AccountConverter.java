package com.waither.userservice.dto.converter;

import com.waither.userservice.dto.request.AccountReqDto;
import com.waither.userservice.entity.Settings;
import com.waither.userservice.entity.User;
import com.waither.userservice.entity.type.UserStatus;

public class AccountConverter {

    public static User toCreateUser(AccountReqDto.RegisterRequestDto requestDto, String encodedPw) {
        return User.builder()
                .email(requestDto.email())
                .password(encodedPw)
                .nickname("추워하는 곰탱이")
                .status(UserStatus.ACTIVE)
                .role("ROLE_USER")
                .custom(true)
                .build();
    }

    public static Settings toCreateSetting(User newUser) {
        return Settings.builder()
                .user(newUser)
                .build();
    }
}
