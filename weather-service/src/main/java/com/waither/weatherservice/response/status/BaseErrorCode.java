package com.waither.weatherservice.response.status;

import org.springframework.http.HttpStatus;

import com.waither.weatherservice.response.ApiResponse;

public interface BaseErrorCode {

	HttpStatus getHttpStatus();

	String getCode();

	String getMessage();

	ApiResponse<Void> getErrorResponse();
}
