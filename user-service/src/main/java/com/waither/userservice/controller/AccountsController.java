package com.waither.userservice.controller;

import com.waither.userservice.dto.request.AccountReqDto;
import com.waither.userservice.entity.User;
import com.waither.userservice.global.annotation.AuthUser;
import com.waither.userservice.jwt.dto.JwtDto;
import com.waither.userservice.service.AccountsService;
import com.waither.userservice.global.response.ApiResponse;
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

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody AccountReqDto.RegisterRequestDto requestDto) {
        accountsService.signup(requestDto);
        // SignUp 때만 201 Created 사용
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.onSuccess(HttpStatus.CREATED, "회원가입이 성공적으로 완료되었습니다.")
                );
    }

    // Jwt 토큰 재발급
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
    public ApiResponse<String> verificationEmail(@RequestBody AccountReqDto.EmailVerificationDto verificationDto) {
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

    // 닉네임 변경
    @PatchMapping("/update-nickname")
    public ApiResponse<String> updateNickname(@AuthUser User user,
                                              @RequestBody AccountReqDto.NicknameDto nicknameDto) {
        accountsService.updateNickname(user, nicknameDto.nickname());
        return ApiResponse.onSuccess("닉네임이 " + nicknameDto.nickname() + "로 바뀌었습니다.");
    }

    // 비밀번호 확인
    @PostMapping("/password-check")
    public ApiResponse<String> passwordCheckEmail(@AuthUser User user,
                                                  @RequestBody AccountReqDto.PasswordCheckDto passwordCheckDto) {
            accountsService.checkPassword(user, passwordCheckDto.password());
        return ApiResponse.onSuccess("비밀번호가 확인되었습니다.");
    }

    // 비밀번호 변경
    @PatchMapping("/update-password")
    public ApiResponse<String> updatePassword(@AuthUser User user,
                                              @Valid @RequestBody AccountReqDto.UpdatePasswordDto updatePasswordDto) {
        accountsService.updatePassword(user, updatePasswordDto.password());
        return ApiResponse.onSuccess("비밀번호가 변경되었습니다.");
    }


    // Todo : 삭제 다시 ; soft delete로 변경 예정
    @GetMapping("/emails/delete")
    public void deleteUser(@RequestParam Long userId) {
        accountsService.deleteUser(userId);
    }
}
