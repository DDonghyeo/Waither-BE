package com.waither.weatherservice.exception;

import com.waither.weatherservice.response.status.BaseErrorCode;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final BaseErrorCode errorCode;

	public CustomException(BaseErrorCode errorCode) {
		this.errorCode = errorCode;
	}
}
