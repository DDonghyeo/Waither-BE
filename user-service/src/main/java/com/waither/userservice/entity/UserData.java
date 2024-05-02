package com.waither.userservice.entity;

import com.waither.userservice.entity.type.Season;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_data")
@Entity
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 각 답변의 평균 값 (level 1 쪽이 추움 ~ level 5 쪽이 더움)
    private Double level1;
    private Double level2;
    private Double level3;
    private Double level4;
    private Double level5;

    // 사용자 데이터 가중치
    private Double weight;

    // 계절
    @Enumerated(EnumType.STRING)
    private Season season;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}