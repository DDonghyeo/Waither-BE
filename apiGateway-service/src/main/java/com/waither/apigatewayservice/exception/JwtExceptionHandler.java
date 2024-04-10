package com.waither.apigatewayservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waither.apigatewayservice.util.SeverHttpResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class JwtExceptionHandler implements ErrorWebExceptionHandler {
    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        // 만료된 JWT 예외 처리
        if (ex instanceof ExpiredJwtException) {
            return SeverHttpResponseUtil.sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");
        }
        // 다른 JWT 관련 예외 처리
        else if (ex instanceof SignatureException ||
                ex instanceof MalformedJwtException ||
                ex instanceof UnsupportedJwtException ||
                ex instanceof IllegalArgumentException) {
            return SeverHttpResponseUtil.sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다. 다시 로그인해주세요.");
        }

        return SeverHttpResponseUtil.sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "인가 과정 중 서버 에러가 발생했습니다.");
    }
}
