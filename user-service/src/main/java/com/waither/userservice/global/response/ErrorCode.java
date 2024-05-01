package com.waither.userservice.global.response;

import com.waither.userservice.global.response.status.BaseErrorCode;
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
    INVALID_CODE(HttpStatus.BAD_REQUEST, "EMAIL400_0", "인증번호가 일치하지 않아요. 다시 한 번 확인해주세요."),
    INVALID_Account(HttpStatus.BAD_REQUEST, "EMAIL400_1", "인증되지 않은 이메일입니다."),
    AUTH_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL400_1", "다시 인증 번호를 요청해주세요."),
    VERIFIED_CHECK_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL400_1", "인증 완료 후 유효기간이 경과하였습니다. 다시 인증 번호를 요청해주세요."),

    UNABLE_TO_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL500_0", "이메일을 전송하는 도중, 에러가 발생했습니다."),
    NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL500_1", "이메일 인증 코드를 생성할 수 없습니다."),

    DATA_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER404_0", "해당 데이터를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER404_1", "사용자가 없습니다."),
    EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER400_2", "이메일은 필수 입니다."),
    PASSWORD_NOT_EQUAL(HttpStatus.BAD_REQUEST, "USER400_3", "비밀번호가 일치하지 않습니다."),
    CURRENT_PASSWORD_NOT_EQUAL(HttpStatus.BAD_REQUEST, "USER400_4", "현재 비밀번호가 일치하지 않습니다."),
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER400_5", "사용자가 이미 존재합니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
