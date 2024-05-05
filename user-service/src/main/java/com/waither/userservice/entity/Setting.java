package com.waither.userservice.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

// 코드 일부 생략

@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "settings")
@Entity
@DynamicInsert
@DynamicUpdate
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 맞춤 서비스 제공
    @Column(name = "custom")
    @ColumnDefault("true")
    private boolean custom;

    // 외출 알림
    @Column(name = "outAlert")
    @ColumnDefault("false")
    private boolean outAlert;

    // 외출 시간
    @Column(name = "outTime")
    private LocalTime outTime;

    // 월 ~ 금 알림
    @Column(name = "sun")
    @ColumnDefault("false")
    private boolean sun;
    @Column(name = "mon")
    @ColumnDefault("false")
    private boolean mon;
    @Column(name = "tue")
    @ColumnDefault("false")
    private boolean tue;
    @Column(name = "wed")
    @ColumnDefault("false")
    private boolean wed;
    @Column(name = "thu")
    @ColumnDefault("false")
    private boolean thu;
    @Column(name = "fri")
    @ColumnDefault("false")
    private boolean fri;
    @Column(name = "sat")
    @ColumnDefault("false")
    private boolean sat;

    // 기상 특보 알림
    @Column(name = "climateAlert")
    @ColumnDefault("true")
    private boolean climateAlert;

    // 사용자 맞춤 예보 받기
    @Column(name = "userAlert")
    @ColumnDefault("true")
    private boolean userAlert;

    // 강설 정보 알림
    @Column(name = "snowAlert")
    @ColumnDefault("true")
    private boolean snowAlert;

    // 바람 세기 알림
    @Column(name = "windAlert")
    @ColumnDefault("true")
    private boolean windAlert;
    // 바람세기 정도
    @Column(name = "windDegree")
    @ColumnDefault("10")
    private Integer windDegree;

    // 직장 지역 레포트 알림 받기
    @Column(name = "regionReport")
    @ColumnDefault("true")
    private boolean regionReport;

    // 강수량 보기
    @Column(name = "precipitation")
    @ColumnDefault("true")
    private boolean precipitation;

    // 풍량/풍속 보기
    @Column(name = "wind")
    @ColumnDefault("true")
    private boolean wind;

    // 미세먼지 보기
    @Column(name = "dust")
    @ColumnDefault("true")
    private boolean dust;

    // Id에 Setter 쓰지 않기 위해, 명시적으로 지정
    public void setId(Long id) {
    }

}