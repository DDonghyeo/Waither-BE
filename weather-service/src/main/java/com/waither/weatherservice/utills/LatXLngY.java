package com.waither.weatherservice.utills;

import lombok.Builder;

@Builder
public record LatXLngY(
	double lat,
	double lng,
	double x,
	double y
) {
}
