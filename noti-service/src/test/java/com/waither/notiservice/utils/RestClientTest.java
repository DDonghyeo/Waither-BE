package com.waither.notiservice.utils;

import com.waither.notiservice.api.request.LocationDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RestClientTest {

    @Test
    void transferToRegion() {

        LocationDto location = LocationDto.builder()
                .longitude(36)
                .latitude(127)
                .build();

        String region = RestClient.transferToRegion(location);

        assert(region).equals("서울특별시");
    }
}