package com.waither.weatherservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GetWeatherRequest(
	@NotBlank(message = "[ERROR] 위도 입력은 필수 입니다.")
	double latitude,
	@NotBlank(message = "[ERROR] 경도 입력은 필수 입니다.")
	double longitude
) {
}
