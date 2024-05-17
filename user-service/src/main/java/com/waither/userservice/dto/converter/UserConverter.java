package com.waither.userservice.dto.converter;

import com.waither.userservice.dto.request.UserReqDto;
import com.waither.userservice.entity.User;
import com.waither.userservice.entity.type.UserStatus;

public class UserConverter {

    public static User toUser(UserReqDto.SignUpRequestDto requestDto, String encodedPw) {
        return User.builder()
                .email(requestDto.email())
                .password(encodedPw)
                .nickname("추워하는 곰탱이")
                .status(UserStatus.ACTIVE)
                .role("ROLE_USER")
                .custom(true)
                .build();
    }


}
