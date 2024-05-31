package com.waither.notiservice.domain.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash
public class NotificationRecord {

    //유저 식별자
    private String email;

    //마지막 강수 알림 받은 시간
    private LocalDateTime lastRainAlarmReceived;

    //마지막 바람세기 알림 받은 시간
    private LocalDateTime lastWindAlarmReceived;

    //사용자 마지막 위치 (위도)
    private Double lat;

    //사용자 마지막 위치 (경도)
    private Double lon;
}
