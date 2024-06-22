package com.waither.userservice.controller;

import com.waither.userservice.dto.response.KakaoResDto;
import com.waither.userservice.global.response.ApiResponse;
import com.waither.userservice.service.KakaoService;
import com.waither.userservice.service.commandService.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/oauth")
public class OAuthController {

    private final KakaoService kakaoService;

    private final UserService userService;

    @Operation(hidden = true)
    @GetMapping("/kakao/callback")
    public ApiResponse<?> callback(@RequestParam("code") String code) {

        String accessTokenFromKakao = kakaoService.getAccessTokenFromKakao(code);

        KakaoResDto.UserInfoResponseDto userInfo = kakaoService.getUserInfo(accessTokenFromKakao);

        String email = userInfo.getKakaoAccount().getEmail();
        if (!userService.isUserRegistered(email)) {
            userService.signup(userInfo);
        }

        return ApiResponse.onSuccess(userService.provideTokenForOAuth(email));
    }
}
