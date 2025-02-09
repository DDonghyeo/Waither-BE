package com.waither.userservice.global.jwt.execption;

import com.waither.userservice.global.response.ApiResponse;
import com.waither.userservice.global.jwt.util.HttpResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("Access Denied: ", accessDeniedException);

        HttpResponseUtil.setErrorResponse(
                response,
                SecurityErrorCode.FORBIDDEN.getHttpStatus(),
                ApiResponse.onFailure(
                        SecurityErrorCode.FORBIDDEN.getCode(),
                        SecurityErrorCode.FORBIDDEN.getMessage(),
                        accessDeniedException.getMessage()
                )
        );

    }
}

