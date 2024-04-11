package com.waither.userservice.jwt.execption;

import com.waither.userservice.global.response.ApiResponse;
import com.waither.userservice.jwt.util.HttpResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        log.error(">>>>>> AuthenticationException: ", authException);

        HttpResponseUtil.setErrorResponse(
                response,
                SecurityErrorCode.UNAUTHORIZED.getHttpStatus(),
                ApiResponse.onFailure(
                        SecurityErrorCode.UNAUTHORIZED.getCode(),
                        SecurityErrorCode.UNAUTHORIZED.getMessage(),
                        authException.getMessage()
                )
        );
    }
}
