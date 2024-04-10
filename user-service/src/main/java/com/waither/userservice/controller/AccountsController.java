package com.waither.userservice.controller;

import com.waither.userservice.dto.EmailVerificationDto;
import com.waither.userservice.dto.RegisterRequestDto;
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
        try {
            jwtUtil.isRefreshToken(refreshToken);
            return ApiResponse.onSuccess(jwtUtil.reissueToken(refreshToken));
        } catch (ExpiredJwtException eje) {
            throw new SecurityCustomException(SecurityErrorCode.TOKEN_EXPIRED, eje);
        } catch (IllegalArgumentException iae) {
            throw new SecurityCustomException(SecurityErrorCode.INVALID_TOKEN, iae);
        }
    }

    // 이메일에 인증번호 보내기
    @GetMapping("/emails/submit-authcode")
    public ApiResponse<String> submitAuthCode(@RequestParam String email) {
        try {
            accountsService.sendAuthCodeToEmail(email);
            return ApiResponse.onSuccess("인증번호 전송에 성공했습니다.");
        } catch (CustomException e) {
            throw e; // 이미 CustomException이 발생한 경우에는 다시 던지기만 하면 됨
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNABLE_TO_SEND_EMAIL);
        }
    }

    // 이메일 인증하기
    @PostMapping("/emails/verifications")
    public ApiResponse<String> verificationEmail(@RequestBody EmailVerificationDto verificationDto) {
        accountsService.verifyCode(verificationDto.email(), verificationDto.authCode());

        return ApiResponse.onSuccess("이메일 인증에 성공했습니다.");
    }

    // 임시 비밀번호 발급
    @GetMapping("/emails/temporary-password")
    public ApiResponse<String> submitTemporaryPassword(@RequestParam String email) {
        try {
            accountsService.checkUserExists(email);
            String tempPassword = accountsService.sendTempPassword(email);
            accountsService.changeToTempPassword(email, tempPassword);
            return ApiResponse.onSuccess("인증번호 전송에 성공했습니다.");
        } catch (CustomException e) {
            throw e; // 내부에서 일어난 exception
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNABLE_TO_SEND_EMAIL);
        }

    }

}
