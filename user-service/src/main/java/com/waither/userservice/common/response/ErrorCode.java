package com.waither.userservice.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    // 일반적인 ERROR 응답
    BAD_REQUEST_400(HttpStatus.BAD_REQUEST,
            "COMMON400",
            HttpStatus.BAD_REQUEST.getReasonPhrase()),
    UNAUTHORIZED_401(HttpStatus.UNAUTHORIZED,
            "COMMON401",
            HttpStatus.UNAUTHORIZED.getReasonPhrase()),
    FORBIDDEN_403(HttpStatus.FORBIDDEN,
            "COMMON403",
            HttpStatus.FORBIDDEN.getReasonPhrase()),
    NOT_FOUND_404(HttpStatus.NOT_FOUND,
            "COMMON404",
            HttpStatus.NOT_FOUND.getReasonPhrase()),
    INTERNAL_SERVER_ERROR_500(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500",
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()),

    // 유효성 검사
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID400_0", "입력값에 대한 검증에 실패했습니다."),

    // 멤버 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER400_1", "사용자가 없습니다."),
    EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER400_2", "이메일은 필수 입니다."),
    PASSWORD_NOT_EQUAL(HttpStatus.BAD_REQUEST, "USER400_3", "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER400_4", "사용자가 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
