package com.waither.apigatewayservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        Map<String, Object> responseBody = new HashMap<>();

        // 만료된 JWT 예외 처리
        if (ex instanceof ExpiredJwtException) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody.put("code", "EXPIRED");
            responseBody.put("message", "Access Token is Expired!");
        }
        // 다른 JWT 관련 예외 처리
        else if (ex instanceof SignatureException ||
                ex instanceof MalformedJwtException ||
                ex instanceof UnsupportedJwtException ||
                ex instanceof IllegalArgumentException) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody.put("code", "INVALID");
            responseBody.put("message", "Invalid Access Token");
        }
        // 다른 예상치 못한 예외 처리
        else {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody.put("code", "INTERNAL_SERVER_ERROR");
            responseBody.put("message", "An unexpected error occurred");
        }

        // 응답 본문을 DataBuffer로 변환하고 응답에 작성
        DataBuffer wrap = null;
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(responseBody);
            wrap = exchange.getResponse().bufferFactory().wrap(bytes);
        } catch (JsonProcessingException e) {
            // JSON 처리 중 예외 발생, 에러를 로깅
            log.error("JSON 응답 처리 중 오류 발생: {}", e.getMessage());
            // 응답 본문을 작성하지 않고 응답을 완료로 설정
            return exchange.getResponse().setComplete();
        }

        // DataBuffer가 성공적으로 생성되면 응답 본문을 작성
        if (wrap != null) {
            return exchange.getResponse().writeWith(Flux.just(wrap));
        } else {
            return exchange.getResponse().setComplete();
        }
    }

}
