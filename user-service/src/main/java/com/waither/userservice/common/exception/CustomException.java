package com.waither.userservice.common.exception;

import com.waither.userservice.common.response.BaseErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public CustomException(BaseErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
