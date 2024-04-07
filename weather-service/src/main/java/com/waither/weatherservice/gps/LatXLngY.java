package com.waither.weatherservice.gps;

import lombok.Builder;

@Builder
public record LatXLngY(
	double lat,
	double lng,
	double x,
	double y
) {
}
