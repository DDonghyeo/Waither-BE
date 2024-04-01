package com.waither.userservice.common.exception;

import com.waither.userservice.common.response.ApiResponse;
import com.waither.userservice.common.response.BaseErrorCode;
import com.waither.userservice.common.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 모든 예외에 대한 처리
    // 표준 예외를 적극적으로 사용
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResponse<String>> handleAllException(Exception e) {
        log.error("[WARNING] Internal Server Error : {} ", e.getMessage());
        BaseErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR_500;
        ApiResponse<String> errorResponse = ApiResponse.onFailure(
                errorCode.getCode(),
                errorCode.getMessage(),
                e.getMessage()
        );
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    // 데이터 무결성 위반 예외에 대한 처리
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ApiResponse<Void>> handleIntegrityConstraint(DataIntegrityViolationException e) {
        log.warn(">>>>> Data Integrity Violation Exception : {}", e.getMessage());
        BaseErrorCode errorCode = ErrorCode.USER_ALREADY_EXIST;
        return ResponseEntity.
                status(errorCode.getHttpStatus())
                .body(errorCode.getErrorResponse());
    }

    // @Valid 유효성 검사를 실패했을 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        // 실패한 validation 을 담을 Map
        Map<String, String> failedValidations = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        // fieldErrors 를 순회하며 failedValidations 에 담는다.
        fieldErrors.forEach(error -> failedValidations.put(error.getField(), error.getDefaultMessage()));
        ApiResponse<Map<String, String>> errorResponse = ApiResponse.onFailure(
                ErrorCode.VALIDATION_FAILED.getCode(),
                ErrorCode.VALIDATION_FAILED.getMessage(),
                failedValidations);
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    // Custom 예외에 대한 처리
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.warn("[WARNING] Custom Exception : {}", e.getMessage());
        BaseErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.
                status(errorCode.getHttpStatus())
                .body(errorCode.getErrorResponse());
    }

}