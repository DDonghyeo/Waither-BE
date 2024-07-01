package com.waither.notiservice.api.response;

import com.waither.notiservice.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        String id,
        LocalDateTime time,
        String message
) {
    public static NotificationResponse of(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .time(notification.getCreatedAt())
                .message(notification.getContent())
                .build();
    }
}
