package com.waither.userservice.entity;

import com.waither.userservice.entity.type.Season;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "survey")
@Entity
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 온도
    private Double temp;

    // 답변 (1~5 정도로 저장)
    private Integer ans; // 답변을 숫자로 저장

    // 계절
    @Enumerated(EnumType.STRING)
    private Season season;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", temp=" + temp +
                ", ans='" + ans + '\'' +
                ", season=" + season +
                '}';
    }
}
