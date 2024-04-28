package com.waither.notiservice.global.response;


import com.waither.notiservice.global.response.status.BaseErrorCode;
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

    // 이메일 관련 에러
    NO_USER_MEDIAN_REGISTERED(HttpStatus.NOT_FOUND, "USER404_0", "사용자 설정값이 존재하지 않습니다."),
    NO_USER_DATA_REGISTERED(HttpStatus.NOT_FOUND, "USER404_1", "사용자 데이터 값이 존재하지 않습니다."),

    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
