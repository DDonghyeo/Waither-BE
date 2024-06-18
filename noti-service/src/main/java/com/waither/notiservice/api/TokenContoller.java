package com.waither.notiservice.api;

import com.waither.notiservice.api.request.TokenDto;
import com.waither.notiservice.global.annotation.AuthUser;
import com.waither.notiservice.global.response.ApiResponse;
import com.waither.notiservice.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/noti")
@RestController
public class TokenContoller {

    private final AlarmService alarmService;

    @Operation(summary = "Firebase Token 업데이트", description = "Request Body에 발급한 FCM토큰 값을 넣어서 주시면 됩니다.")
    @PostMapping("/token")
    public ApiResponse<?> updateToken(@AuthUser String email, @RequestBody TokenDto tokenDto) {
        alarmService.updateToken(email, tokenDto);
        return ApiResponse.onSuccess("토큰 업로드가 완료되었습니다.");
    }
}
