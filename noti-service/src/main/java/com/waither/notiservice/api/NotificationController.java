package com.waither.notiservice.api;

import com.waither.notiservice.api.request.LocationDto;
import com.waither.notiservice.global.annotation.AuthUser;
import com.waither.notiservice.global.response.ApiResponse;
import com.waither.notiservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/noti")
@RestController
public class NotificationController {

    private final NotificationService notificationService;


    @Operation(summary = "Get notification", description = "알림 목록 조회하기")
    @GetMapping("")
    public ApiResponse<?> getNotifications(@AuthUser String email) {
        return ApiResponse.onSuccess(notificationService.getNotifications(email));
    }

    @Operation(summary = "Delete notification", description = "알림 삭제하기")
    @DeleteMapping("")
    public ApiResponse<?> deleteNotification(@AuthUser String email, @RequestParam("id") String notificationId) {
        notificationService.deleteNotification(email, notificationId);
        return ApiResponse.onSuccess(HttpStatus.OK);
    }

    @Operation(summary = "Send Go Out Alarm", description = "외출 알림 전송하기")
    @PostMapping("/goOut")
    public ApiResponse<?> sendGoOutAlarm(@AuthUser String email) {
        notificationService.sendGoOutAlarm(email);
        return ApiResponse.onSuccess(HttpStatus.OK);
    }

    @Operation(summary = "Current Location", description = "현재 위치 전송")
    @PostMapping("/location")
    public ApiResponse<?> updateLocation(@AuthUser String email, @RequestBody @Valid LocationDto locationDto) {
        notificationService.updateLocation(email, locationDto);
        return ApiResponse.onSuccess(HttpStatus.OK);
    }

}
