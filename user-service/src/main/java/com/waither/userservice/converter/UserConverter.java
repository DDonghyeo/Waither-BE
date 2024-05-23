package com.waither.userservice.converter;

import com.waither.userservice.dto.request.UserReqDto;
import com.waither.userservice.entity.User;
import com.waither.userservice.entity.enums.UserStatus;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class UserConverter {

    public static User toUser(UserReqDto.SignUpRequestDto requestDto, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(requestDto.email())
                .password(passwordEncoder.encode(requestDto.password()))
                .nickname("추워하는 곰탱이")
                .status(UserStatus.ACTIVE)
                .role("ROLE_USER")
                .custom(true)
                .build();
    }

}
