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
@RequestMapping("/oauth")
public class OAuthController {

    private final KakaoService kakaoService;

    private final AccountsService accountsService;

    @GetMapping("/callback")
    public ApiResponse<?> callback(@RequestParam("code") String code) {

        String accessTokenFromKakao = kakaoService.getAccessTokenFromKakao(code);

        KakaoResDto.UserInfoResponseDto userInfo = kakaoService.getUserInfo(accessTokenFromKakao);

        //TODO: if 회원가입 안 한 상태 -> 회원가입 진행

        //TODO: 토큰 발급
        //accountsService
        return ApiResponse.onSuccess(null);
    }
}
