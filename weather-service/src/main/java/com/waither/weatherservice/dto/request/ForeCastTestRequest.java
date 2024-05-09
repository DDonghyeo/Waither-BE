package com.waither.weatherservice.dto.request;

public record ForeCastTestRequest(
	int nx,
	int ny,
	String baseDate,
	String baseTime
) {
}
