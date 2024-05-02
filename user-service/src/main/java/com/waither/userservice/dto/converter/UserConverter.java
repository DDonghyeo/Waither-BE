package com.waither.userservice.dto.converter;

import com.waither.userservice.dto.request.UserReqDto;
import com.waither.userservice.entity.User;
import com.waither.userservice.entity.type.UserStatus;

public class UserConverter {


    public static User toCreateNewUser(UserReqDto.RegisterRequestDto requestDto, String encodedPw) {
        return User.builder()
                .email(requestDto.email())
                .password(encodedPw)
                // Todo : 자동 닉네임 생성기 만들기
                .nickname("추워하는 곰탱이")
                .status(UserStatus.ACTIVE)
                .role("ROLE_USER")
                .custom(true)
                .build();
    }
}
