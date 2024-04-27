package com.waither.notiservice.api;

import com.waither.notiservice.api.response.NotificationResponse;
import com.waither.notiservice.dto.LocationDto;
import com.waither.notiservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/noti")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "get notification", description = "알림 목록 조회하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping("")
    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationService.getNotifications(userId);
    }

    @Operation(summary = "Send Go Out Alarm", description = "외출 알림 전송하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @PostMapping("/goOut")
    public void sendGoOutAlarm(Long userId) {
        notificationService.sendGoOutAlarm(userId);
    }

    @PostMapping("/")
    public void checkCurrentAlarm(@RequestBody LocationDto locationDto) {
        notificationService.checkCurrentAlarm(locationDto);
    }
}
