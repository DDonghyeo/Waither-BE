package com.waither.weatherservice.dto;

public record ForeCastTestRequest(
	int nx,
	int ny,
	String baseDate,
	String baseTime
) {
}
