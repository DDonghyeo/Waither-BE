package com.waither.userservice.accounts.controller;

import com.waither.userservice.accounts.dto.RegisterRequestDto;
import com.waither.userservice.accounts.jwt.dto.JwtDto;
import com.waither.userservice.accounts.jwt.util.JwtUtil;
import com.waither.userservice.accounts.service.AccountsService;
import com.waither.userservice.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/")
public class AccountsController {

    private final AccountsService accountsService;
    private final JwtUtil jwtUtil;

    @GetMapping("/test")
    public String Test() {
        return "모든 인가를 마치고, test Controller에 도달했습니다.";
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        accountsService.signup(requestDto);
        // SignUp 때만 201 Created 사용
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.onSuccess(HttpStatus.CREATED, "회원가입이 성공적으로 완료되었습니다.")
                );
    }

    @GetMapping("/reissue")
    public ApiResponse<JwtDto> reissueToken(@RequestHeader("RefreshToken") String refreshToken) {
        return ApiResponse.onSuccess(jwtUtil.reissueToken(refreshToken));
    }
}
