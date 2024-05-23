package com.waither.userservice.converter;

import com.waither.userservice.entity.Region;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RegionConverter {

    // Region 기본값으로 설정
    public static Region createRegion() {
        // Region 기본값으로 설정
        return Region.builder()
                .regionName("서울시")
                .longitude(37.5665)
                .latitude(126.9780)
                .build();
    }
}
