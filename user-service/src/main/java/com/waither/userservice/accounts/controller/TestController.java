package com.waither.userservice.accounts.controller;

import com.waither.userservice.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> Test() {

        // 사용자 생성 등 200 외 상태 코드 간헐적으로 사용할 때
        // 해당 방식 이용
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.onSuccess(HttpStatus.CREATED, "회원가입이 완료되었습니다.")
                );
    }


}
