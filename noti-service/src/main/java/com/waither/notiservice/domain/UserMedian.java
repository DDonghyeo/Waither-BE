package com.waither.notiservice.domain;

import com.waither.notiservice.domain.type.Season;
import com.waither.notiservice.dto.kafka.KafkaDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_median")
@DynamicInsert
@Entity
public class UserMedian {

    @Id
    private Long userId;
    private Double medianOf1And2;
    private Double medianOf2And3;
    private Double medianOf3And4;
    private Double medianOf4And5;

    @Enumerated(value = EnumType.STRING)
    public Season season;

    public void setLevel(String level, Double value) {
        switch (level) {
            case "medianOf1And2" -> medianOf1And2 = value;
            case "medianOf2And3" -> medianOf2And3 = value;
            case "medianOf3And4" -> medianOf3And4 = value;
            case "medianOf4And5" -> medianOf4And5 = value;
        }
    }

    public void setLevel(KafkaDto.UserMedianDto userMedianDto) {
        userMedianDto.medians().forEach(median ->
                median.forEach(this::setLevel)
        );
    }
}
