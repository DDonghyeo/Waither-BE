package com.waither.userservice.entity;

import com.waither.userservice.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "settings")
@Entity
// 알림 설정 관련 Entity
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외출 시간
    private LocalDateTime outTime;

    // 외출 알림
    private boolean outAlert;

    // 월 ~ 금 알림
    private boolean sun;
    private boolean mon;
    private boolean tue;
    private boolean wed;
    private boolean thu;
    private boolean fri;
    private boolean sat;

    // 기상 특보 알림
    private boolean climateAlert;

    // 사용자 맞춤 알림
    private boolean userAlert;

    // 강설 정보 알림
    private boolean snowAlert;

    // 바람 세기 알림
    private boolean windAlert;

    // 바람세기 정도
    private Integer windDegree;

    // 직장 지역 레포트 알림 받기
    private boolean regionReport;

    // 강수량 보기
    private boolean precipitation;

    // 풍량/풍속 보기
    private boolean wind;

    // 미세먼지 보기
    private boolean dust;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}