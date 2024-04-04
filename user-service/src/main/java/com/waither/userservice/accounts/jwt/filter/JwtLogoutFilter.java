package com.waither.userservice.accounts.jwt.filter;

import com.waither.userservice.accounts.jwt.util.HttpResponseUtil;
import com.waither.userservice.accounts.jwt.util.JwtUtil;
import com.waither.userservice.accounts.jwt.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class JwtLogoutFilter implements LogoutHandler {

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            log.info("[*] Logout Filter");

            String accessToken = jwtUtil.resolveAccessToken(request);

            String email = jwtUtil.getEmail(accessToken);

            // RefreshToken 삭제
            redisUtil.delete(email);


        } catch (ExpiredJwtException e) {
            try {
                HttpResponseUtil.setErrorResponse(response, HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다. 다시 로그인하세요");
            } catch (IOException ex) {
                log.error("IOException occurred while setting error response: {}", ex.getMessage());
            }
        }
    }
}
