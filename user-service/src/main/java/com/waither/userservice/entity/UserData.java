package com.waither.userservice.entity;

import com.waither.userservice.converter.SurveyConverter;
import com.waither.userservice.entity.enums.Season;
import com.waither.userservice.global.BaseEntity;
import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_data")
@Entity
public class UserData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 각 답변의 평균 값 (level 1 쪽이 추움 ~ level 5 쪽이 더움)
    private Double level1;
    private Double level2;
    private Double level3;
    private Double level4;
    private Double level5;

    // 계절
    @Enumerated(EnumType.STRING)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    // UserData 기본값으로 설정
    public static UserData createUserData(Season season) {
        switch (season) {
            case SPRING_AUTUMN:
                return UserData.builder()
                        .level1(10.0)
                        .level2(17.0)
                        .level3(24.0)
                        .level4(27.0)
                        .level5(30.0)
                        .season(season)
                        .build();
            case SUMMER:
                return UserData.builder()
                        .level1(15.0)
                        .level2(24.0)
                        .level3(30.0)
                        .level4(33.0)
                        .level5(36.0)
                        .season(season)
                        .build();
            case WINTER:
                return UserData.builder()
                        .level1(-17.0)
                        .level2(-7.0)
                        .level3(0.0)
                        .level4(6.0)
                        .level5(12.0)
                        .season(season)
                        .build();
            default:
                throw new CustomException(ErrorCode.INVALID_SEASON);
        }
    }

    public static List<UserData> createUserDataList(User user) {
        return Arrays.stream(Season.values())
                .map(season -> {
                    UserData userData = createUserData(season);
                    // 연관관계 설정
                    userData.setUser(user);
                    return userData;
                })
                .toList();
    }

    public Double getLevel(int level) {
        return switch (level) {
            case 1 -> level1;
            case 2 -> level2;
            case 3 -> level3;
            case 4 -> level4;
            case 5 -> level5;
            default -> throw new CustomException(ErrorCode.INVALID_LEVEL_VALUE);
        };
    }

    public void updateLevelValue(int level, double newValue) {
        switch (level) {
            case 1:
                this.level1 = newValue;
                break;
            case 2:
                this.level2 = newValue;
                break;
            case 3:
                this.level3 = newValue;
                break;
            case 4:
                this.level4 = newValue;
                break;
            case 5:
                this.level5 = newValue;
                break;
            default:
                throw new CustomException(ErrorCode.INVALID_LEVEL_VALUE);
        }
    }

}