package com.waither.userservice.controller;

import com.waither.userservice.dto.response.KakaoResDto;
import com.waither.userservice.global.response.ApiResponse;
import com.waither.userservice.service.AccountsService;
import com.waither.userservice.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/oauth")
public class OAuthController {

    private final KakaoService kakaoService;

    private final AccountsService accountsService;

    @GetMapping("/kakao/callback")
    public ApiResponse<?> callback(@RequestParam("code") String code) {

        String accessTokenFromKakao = kakaoService.getAccessTokenFromKakao(code);

        KakaoResDto.UserInfoResponseDto userInfo = kakaoService.getUserInfo(accessTokenFromKakao);

        String email = userInfo.getKakaoAccount().getEmail();
        if (!accountsService.isUserRegistered(email)) {
            accountsService.signup(userInfo);
        }

        return ApiResponse.onSuccess(accountsService.provideTokenForOAuth(email));
    }
}
