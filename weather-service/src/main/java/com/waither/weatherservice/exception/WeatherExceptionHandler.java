package com.waither.weatherservice.exception;

import com.waither.weatherservice.response.status.BaseErrorCode;

public class WeatherExceptionHandler extends CustomException {
	public WeatherExceptionHandler(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
