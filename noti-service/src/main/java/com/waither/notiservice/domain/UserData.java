package com.waither.notiservice.domain;

import com.waither.notiservice.domain.type.Season;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_data")
@DynamicInsert
@Entity
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String nickName;

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


}
