package com.waither.weatherservice.dto.request;

public record GetWeatherRequest(
	double latitude,
	double longitude
) {
}
