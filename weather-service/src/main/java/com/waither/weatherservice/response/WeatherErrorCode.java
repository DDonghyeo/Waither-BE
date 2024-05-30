package com.waither.weatherservice.response;

import org.springframework.http.HttpStatus;

import com.waither.weatherservice.response.status.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeatherErrorCode implements BaseErrorCode {

	WEATHER_EXAMPLE_ERROR(HttpStatus.BAD_REQUEST, "WEAT4000", "날씨 에러입니다."),
	WEATHER_OPENAPI_ERROR(HttpStatus.BAD_REQUEST, "WEAT4010", "OpenApi 관련 오류입니다."),
	WEATHER_MAIN_ERROR(HttpStatus.BAD_REQUEST, "WEAT4020", "잘못된 위도, 경도입니다."), // 레디스에 캐싱 데이터가 없는 경우
	WEATHER_URI_ERROR(HttpStatus.BAD_REQUEST, "WEAT4030", "URI 변환에 실패하였습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ApiResponse<Void> getErrorResponse() {
		return ApiResponse.onFailure(code, message);
	}
}
