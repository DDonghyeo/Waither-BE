package com.waither.userservice.controller;

import com.waither.userservice.dto.request.UserReqDto;
import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.ErrorCode;
import com.waither.userservice.jwt.dto.JwtDto;
import com.waither.userservice.jwt.execption.SecurityCustomException;
import com.waither.userservice.jwt.execption.SecurityErrorCode;
import com.waither.userservice.jwt.util.JwtUtil;
import com.waither.userservice.service.AccountsService;
import com.waither.userservice.global.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
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

    @GetMapping("/test")
    public String Test() {
        return "모든 인가를 마치고, test Controller에 도달했습니다.";
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody UserReqDto.RegisterRequestDto requestDto) {
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
        JwtDto jwtDto = accountsService.reissueToken(refreshToken);
        return ApiResponse.onSuccess(jwtDto);

    }

    // 이메일에 인증번호 보내기
    @GetMapping("/emails/submit-authcode")
    public ApiResponse<String> submitAuthCode(@RequestBody String email) {
            accountsService.sendAuthCodeToEmail(email);
            return ApiResponse.onSuccess("인증번호 전송에 성공했습니다.");
    }

    // 이메일 인증하기
    @PostMapping("/emails/verifications")
    public ApiResponse<String> verificationEmail(@RequestBody UserReqDto.EmailVerificationDto verificationDto) {
        accountsService.verifyCode(verificationDto.email(), verificationDto.authCode());
        return ApiResponse.onSuccess("이메일 인증에 성공했습니다.");
    }

    // 임시 비밀번호 발급
    @GetMapping("/emails/temporary-password")
    public ApiResponse<String> submitTemporaryPassword(@RequestParam String email) {
            accountsService.checkUserExists(email);
            String tempPassword = accountsService.sendTempPassword(email);
            accountsService.changeToTempPassword(email, tempPassword);
            return ApiResponse.onSuccess("인증번호 전송에 성공했습니다.");
    }

}
