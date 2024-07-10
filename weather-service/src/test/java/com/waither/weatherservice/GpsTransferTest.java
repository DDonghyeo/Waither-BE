package com.waither.weatherservice;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.waither.weatherservice.utills.GpsTransferUtils;
import com.waither.weatherservice.utills.LatXLngY;

@SpringBootTest
class GpsTransferTest {

	@Autowired
	GpsTransferUtils gpsTransfer;

	LatXLngY expected;

	@BeforeEach
	public void setUp() {
		expected = LatXLngY.builder()
			.x(55)
			.y(127)
			.lat(37.582301421185406)
			.lng(126.69838413050432)
			.build();
	}

	@Test
	void convertGpsToGridTest() {

		// given
		double lat = 37.582301421185406;
		double lng = 126.69838413050432;

		// when
		LatXLngY latXLngY = gpsTransfer.convertGpsToGrid(lat, lng);

		// then
		assertEquals(expected, latXLngY);
		System.out.println("[*] 테스트 : " + latXLngY);
	}

	@Test
	void convertGridToGps() {

		// given
		double x = 55;
		double y = 127;

		// when
		LatXLngY latXLngY = gpsTransfer.convertGridToGps(x, y);

		// then
		assertEquals(expected, latXLngY);
		System.out.println("[*] 테스트 : " + latXLngY);
	}
}
