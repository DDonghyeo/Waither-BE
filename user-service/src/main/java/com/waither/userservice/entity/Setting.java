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

    // 외출 알림
    @Column(name = "outAlert")
    private boolean outAlert;

    // 외출 시간
    @Column(name = "outTime")
    private LocalTime outTime;

    // 월 ~ 금 알림
    @Column(name = "sun")
    private boolean sun;
    @Column(name = "mon")
    private boolean mon;
    @Column(name = "tue")
    private boolean tue;
    @Column(name = "wed")
    private boolean wed;
    @Column(name = "thu")
    private boolean thu;
    @Column(name = "fri")
    private boolean fri;
    @Column(name = "sat")
    private boolean sat;

    // 기상 특보 알림
    @Column(name = "climateAlert")
    private boolean climateAlert;

    // 사용자 맞춤 예보 받기
    @Column(name = "userAlert")
    private boolean userAlert;

    // 강설 정보 알림
    @Column(name = "snowAlert")
    private boolean snowAlert;

    // 바람 세기 알림
    @Column(name = "windAlert")
    private boolean windAlert;
    // 바람세기 정도
    @Column(name = "windDegree")
    private Integer windDegree;

    // 직장 지역 레포트 알림 받기
    @Column(name = "regionReport")
    private boolean regionReport;

    // 강수량 보기
    @Column(name = "precipitation")
    private boolean precipitation;

    // 풍량/풍속 보기
    @Column(name = "wind")
    private boolean wind;

    // 미세먼지 보기
    @Column(name = "dust")
    private boolean dust;

    // 사용자 가중치
    @Column(name = "weight")
    private Double weight;

    // Mapping
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "region_id", unique = true)
    private Region region;

    // Id에 Setter 쓰지 않기 위해, 명시적으로 지정
    public void setId(Long id) {
    }

}