package com.waither.userservice.common.response;

import lombok.AccessLevel;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"code", "message", "result"})
public class ApiResponse<T> {


    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(HttpStatus.OK.name(), HttpStatus.OK.getReasonPhrase(), result);
    }

    // 성공한 경우 응답 생성 (상태 코드 지정 가능)
    public static <T> ApiResponse<T> onSuccess(HttpStatus status, T result) {
        return new ApiResponse<>(String.valueOf(status.value()), status.getReasonPhrase(), result);
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T result) {
        return new ApiResponse<>(code, message, result);
    }

    // 실패한 경우 응답 생성 (데이터 없음)
    public static <T> ApiResponse<T> onFailure(String statusCode, String message) {
        return new ApiResponse<>(statusCode, message, null);
    }

    // 삭제된 경우 응답 생성
    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(HttpStatus.NO_CONTENT.getReasonPhrase(), "", null);
    }

}
