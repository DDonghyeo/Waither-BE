package com.waither.apigatewayservice.filter;

import com.waither.apigatewayservice.util.JwtUtil;
import com.waither.apigatewayservice.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CustomLogoutFilter extends AbstractGatewayFilterFactory<CustomLogoutFilter.Config> {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public CustomLogoutFilter(JwtUtil jwtUtil, RedisUtil redisUtil) {
        super(CustomLogoutFilter.Config.class);
        this.jwtUtil = jwtUtil;
        this.redisUtil =redisUtil;
    }

    // GatewayFilter 설정을 위한 Config 클래스
    public static class Config {

    }

    @Override
    public GatewayFilter apply(CustomLogoutFilter.Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // HTTP 요청 헤더에서 Authorization 헤더를 가져옴
            HttpHeaders headers = request.getHeaders();
            if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);

            // JWT 토큰 판별
            String accessToken = authorizationHeader.replace("Bearer ", "");
            log.info("[*] Token exists");

            // Logout 블랙리스트 - Redis에 저장
            redisUtil.save(
                    accessToken,
                    "logout",
                    jwtUtil.getExpTime(accessToken),
                    TimeUnit.MILLISECONDS
            );

            // 사용자 ID를 HTTP 요청 헤더에 추가하여 전달
            ServerHttpRequest newRequest = request.mutate()
                    .build();

            return chain.filter(exchange.mutate().request(newRequest).build());
        };
    }

    // Mono(단일 값), Flux(다중 값) -> Spring WebFlux
    private Mono<Void> onError(ServerWebExchange exchange, String errorMsg, HttpStatus httpStatus) {
        log.error(errorMsg);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }

}
