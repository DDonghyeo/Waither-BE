package com.waither.weatherservice.response;

import org.springframework.http.HttpStatus;

import com.waither.weatherservice.response.status.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeatherErrorCode implements BaseErrorCode {

	WEATHER_EXAMPLE_ERROR(HttpStatus.BAD_REQUEST, "WEAT4000", "날씨 에러입니다."),
	WEATHER_OPENAPI_ERROR(HttpStatus.BAD_REQUEST, "WEAT4001", "OpenApi 관련 오류입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ApiResponse<Void> getErrorResponse() {
		return ApiResponse.onFailure(code, message);
	}
}
