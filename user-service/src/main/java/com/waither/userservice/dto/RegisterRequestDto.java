package com.waither.userservice.dto;

import com.waither.userservice.entity.User;
import com.waither.userservice.entity.type.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "[ERROR] 이메일 입력은 필수입니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "[ERROR] 이메일 형식에 맞지 않습니다.")
        String email,

        @NotBlank(message = "[ERROR] 비밀번호 입력은 필수 입니다.")
        @Size(min = 8, message = "[ERROR] 비밀번호는 최소 8자리 이이어야 합니다.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,64}$", message = "[ERROR] 비밀번호는 8자 이상, 64자 이하이며 특수문자 한 개를 포함해야 합니다.")
        String password
) {
        public User toEntity(String encodedPw) {
                return User.builder()
                        .email(email)
                        .password(encodedPw)
                        // Todo : 자동 닉네임 생성기 만들기
                        .nickname("추워하는 곰탱이")
                        .status(UserStatus.ACTIVE)
                        .role("ROLE_USER")
                        .custom(true)
                        .build();
        }

}
