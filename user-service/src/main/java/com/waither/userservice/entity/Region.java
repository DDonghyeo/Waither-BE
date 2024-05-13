package com.waither.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "region")
@Entity
public class Region {

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

    public void update(String newRegionName, double newLongitude, double newLatitude) {
        regionName = newRegionName;
        longitude = newLongitude;
        latitude = newLatitude;
    }

}
