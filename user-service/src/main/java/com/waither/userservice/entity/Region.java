package com.waither.userservice.entity;

import com.waither.userservice.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "region")
@Entity
public class Region extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 지역명 (String으로)
    @Column(name = "region_name")
    private String regionName;

    // 경도
    @Column(name = "longitude")
    private double longitude;

    // 위도
    @Column(name = "latitude")
    private double latitude;

    // Region 기본값으로 설정
    public static Region createRegion() {
        // Region 기본값으로 설정
        return Region.builder()
                .regionName("서울시")
                .longitude(37.5665)
                .latitude(126.9780)
                .build();
    }

    public void update(String newRegionName, double newLongitude, double newLatitude) {
        regionName = newRegionName;
        longitude = newLongitude;
        latitude = newLatitude;
    }

}
