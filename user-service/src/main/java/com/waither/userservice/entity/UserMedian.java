package com.waither.userservice.entity;

import com.waither.userservice.entity.type.Season;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_median")
@Entity
public class UserMedian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 각 답변의 평균 값 사이 중간값 (level 1 쪽이 추움 ~ level 5 쪽이 더움)
    private Double medianOf1And2;
    private Double medianOf2And3;
    private Double medianOf3And4;
    private Double medianOf4And5;

    // 계절
    @Enumerated(EnumType.STRING)
    private Season season;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}

