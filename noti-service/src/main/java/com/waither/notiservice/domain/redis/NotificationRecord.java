package com.waither.notiservice.domain.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash
public class NotificationRecord {

    //유저 식별자
    @Id
    private String email;

    //마지막 강수 알림 받은 시간
    private LocalDateTime lastRainAlarmReceived;

    //마지막 바람세기 알림 받은 시간
    private LocalDateTime lastWindAlarmReceived;

    //사용자 마지막 위치 (지역)
    private String region;

    public void initializeWindTime() {
        lastWindAlarmReceived = LocalDateTime.now();
    }

    public void initializeRainTime() {
        lastRainAlarmReceived = LocalDateTime.now();
    }

    public void setLastRainAlarmReceived(LocalDateTime lastRainAlarmReceived) {
        this.lastRainAlarmReceived = lastRainAlarmReceived;
    }

    public void setLastWindAlarmReceived(LocalDateTime lastWindAlarmReceived) {
        this.lastWindAlarmReceived = lastWindAlarmReceived;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}
