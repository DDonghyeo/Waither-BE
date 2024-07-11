package com.waither.userservice.entity;

import com.waither.userservice.converter.SurveyConverter;
import com.waither.userservice.entity.enums.Season;
import com.waither.userservice.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static com.waither.userservice.global.util.CalculateUtil.calculateMedian;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_median")
@Entity
public class UserMedian extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    // UserMedian 기본값으로 설정
    public static UserMedian createUserMedian(UserData userData) {
        return UserMedian.builder()
                .medianOf1And2(calculateMedian(userData.getLevel1(), userData.getLevel2()))
                .medianOf2And3(calculateMedian(userData.getLevel2(), userData.getLevel3()))
                .medianOf3And4(calculateMedian(userData.getLevel3(), userData.getLevel4()))
                .medianOf4And5(calculateMedian(userData.getLevel4(), userData.getLevel5()))
                .season(userData.getSeason())
                .build();
    }

    // UserMedianList 생성
    public static List<UserMedian> createUserMedianList(List<UserData> userDataList, User user) {
        return userDataList.stream()
                .map(userData -> {
                    UserMedian userMedian = createUserMedian(userData);
                    // 연관관계 설정
                    userMedian.setUser(user);
                    return userMedian;
                })
                .toList();
    }

    public void updateMedianValue(UserData userData) {
        this.medianOf1And2 = calculateMedian(userData.getLevel1(), userData.getLevel2());
        this.medianOf2And3 = calculateMedian(userData.getLevel2(), userData.getLevel3());
        this.medianOf3And4 = calculateMedian(userData.getLevel3(), userData.getLevel4());
        this.medianOf4And5 = calculateMedian(userData.getLevel4(), userData.getLevel5());
    }

}

