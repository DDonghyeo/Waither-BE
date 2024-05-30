package com.waither.userservice.dto.converter;

import com.waither.userservice.dto.request.AccountReqDto;
import com.waither.userservice.dto.response.KakaoResDto;
import com.waither.userservice.entity.User;
import com.waither.userservice.entity.type.UserStatus;

public class AccountConverter {

    public static User toUser(AccountReqDto.RegisterRequestDto requestDto, String encodedPw) {
        return User.builder()
                .email(requestDto.email())
                .password(encodedPw)
                .nickname("추워하는 곰탱이")
                .status(UserStatus.ACTIVE)
                .role("ROLE_USER")
                .custom(true)
                .build();
    }

    public static User toUser(KakaoResDto.UserInfoResponseDto userInfo) {
        return User.builder()
                .authId(userInfo.getId())
                .nickname(userInfo.getKakaoAccount().getProfile().getNickName())
                .email(userInfo.getKakaoAccount().getEmail())
                .status(UserStatus.ACTIVE)
                .custom(true)
                .role("ROLE_USER")
                .build();
    }

}
