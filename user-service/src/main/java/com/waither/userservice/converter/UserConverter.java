package com.waither.userservice.converter;

import com.waither.userservice.dto.request.UserReqDto;
import com.waither.userservice.dto.response.KakaoResDto;
import com.waither.userservice.entity.User;
import com.waither.userservice.entity.enums.UserStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
