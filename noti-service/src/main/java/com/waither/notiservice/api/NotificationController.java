package com.waither.notiservice.api;

import com.waither.notiservice.api.request.LocationDto;
import com.waither.notiservice.global.response.ApiResponse;
import com.waither.notiservice.service.NotificationService;
import com.waither.notiservice.utils.RedisUtils;
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

    private final RedisUtils redisUtils;

    @Operation(summary = "Get notification", description = "알림 목록 조회하기")
    @GetMapping("")
    public ApiResponse<?> getNotifications(Long userId) {
        return ApiResponse.onSuccess(notificationService.getNotifications(userId));
    }

    @Operation(summary = "Delete notification", description = "알림 삭제하기")
    @DeleteMapping("")
    public ApiResponse<?> deleteNotification(@RequestParam("id") String notificationId) {
        notificationService.deleteNotification(notificationId);
        return ApiResponse.onSuccess(HttpStatus.OK);
    }

    @Operation(summary = "Send Go Out Alarm", description = "외출 알림 전송하기")
    @PostMapping("/goOut")
    public void sendGoOutAlarm(Long userId) {
        notificationService.sendGoOutAlarm(userId);
    }

    @Operation(summary = "Current Location", description = "현재 위치 전송")
    @PostMapping("/location")
    public void checkCurrentAlarm(@RequestBody @Valid LocationDto locationDto) {
        notificationService.checkCurrentAlarm(locationDto);
    }

}
