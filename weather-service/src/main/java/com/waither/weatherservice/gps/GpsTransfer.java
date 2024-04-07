package com.waither.weatherservice.gps;

import org.springframework.stereotype.Component;

@Component
public class GpsTransfer {

	private static final double GRID = 5.0; // 격자 간격(km)
	private static final double RE = 6371.00877 / GRID; // 지구 반경(km)
	private static final double XO = 43; // 기준점 X좌표(GRID)
	private static final double YO = 136; // 기준점 Y좌표(GRID)
	private static final double DEGRAD = Math.PI / 180.0;
	private static final double OLON = 126.0 * DEGRAD; // 기준점 경도(degree)
	private static final double OLAT = 38.0 * DEGRAD; // 기준점 위도(degree)
	private static final double SLAT1 = 30.0 * DEGRAD; // 투영 위도1(degree)
	private static final double SLAT2 = 60.0 * DEGRAD; // 투영 위도2(degree)
	private static final double RADDEG = 180.0 / Math.PI;

	public LatXLngY convertGpsToGrid(double lat, double lng) {
		double sn = Math.tan(Math.PI * 0.25 + SLAT2 * 0.5) / Math.tan(Math.PI * 0.25 + SLAT1 * 0.5);
		sn = Math.log(Math.cos(SLAT1) / Math.cos(SLAT2)) / Math.log(sn);
		double sf = Math.tan(Math.PI * 0.25 + SLAT1 * 0.5);
		sf = Math.pow(sf, sn) * Math.cos(SLAT1) / sn;
		double ro = Math.tan(Math.PI * 0.25 + OLAT * 0.5);
		ro = RE * sf / Math.pow(ro, sn);

		double ra = Math.tan(Math.PI * 0.25 + (lat) * DEGRAD * 0.5);
		ra = RE * sf / Math.pow(ra, sn);
		double theta = lng * DEGRAD - OLON;
		if (theta > Math.PI)
			theta -= 2.0 * Math.PI;
		if (theta < -Math.PI)
			theta += 2.0 * Math.PI;
		theta *= sn;
		double x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
		double y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

		return LatXLngY.builder()
			.lat(lat)
			.lng(lng)
			.x(x)
			.y(y)
			.build();
	}

	public LatXLngY convertGridToGps(double x, double y) {
		double sn = Math.tan(Math.PI * 0.25 + SLAT2 * 0.5) / Math.tan(Math.PI * 0.25 + SLAT1 * 0.5);
		sn = Math.log(Math.cos(SLAT1) / Math.cos(SLAT2)) / Math.log(sn);
		double sf = Math.tan(Math.PI * 0.25 + SLAT1 * 0.5);
		sf = Math.pow(sf, sn) * Math.cos(SLAT1) / sn;
		double ro = Math.tan(Math.PI * 0.25 + OLAT * 0.5);
		ro = RE * sf / Math.pow(ro, sn);
		double xn = x - XO;
		double yn = ro - y + YO;
		double ra = Math.sqrt(xn * xn + yn * yn);
		if (sn < 0.0) {
			ra = -ra;
		}
		double alat = Math.pow((RE * sf / ra), (1.0 / sn));
		alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;
		double theta;
		if (Math.abs(xn) <= 0.0) {
			theta = 0.0;
		} else {
			if (Math.abs(yn) <= 0.0) {
				theta = Math.PI * 0.5;
				if (xn < 0.0) {
					theta = -theta;
				}
			} else
				theta = Math.atan2(xn, yn);
		}
		double alon = theta / sn + OLON;
		double lat = alat * RADDEG;
		double lng = alon * RADDEG;
		return LatXLngY.builder()
			.lat(lat)
			.lng(lng)
			.x(x)
			.y(y)
			.build();
	}
}
