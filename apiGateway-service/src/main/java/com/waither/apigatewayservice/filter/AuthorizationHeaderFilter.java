package com.waither.apigatewayservice.filter;

import com.waither.apigatewayservice.util.JwtUtil;
import com.waither.apigatewayservice.util.RedisUtil;
import com.waither.apigatewayservice.util.SeverHttpResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JwtUtil jwtUtil;

    private final RedisUtil redisUtil;

    public AuthorizationHeaderFilter(JwtUtil jwtUtil, RedisUtil redisUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }

    // GatewayFilter 설정을 위한 Config 클래스
    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // HTTP 요청 헤더에서 Authorization 헤더를 가져옴
            HttpHeaders headers = request.getHeaders();
            if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                return SeverHttpResponseUtil.sendErrorResponse(response,
                        HttpStatus.UNAUTHORIZED,
                        "HTTP 요청 헤더에 Authorization 헤더가 포함되어 있지 않습니다.");
            }

            String authorizationHeader = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);

            // JWT 토큰 가져오기
            String accessToken = authorizationHeader.replace("Bearer ", "");
            log.info("[*] Token exists");

            // JWT 토큰 유효성 검사
            jwtUtil.validateAccessToken(accessToken);

            // logout 처리된 accessToken
            if (redisUtil.hasKey(accessToken) && redisUtil.get(accessToken).equals("logout")) {
                log.info("[*] Logout accessToken");
                return SeverHttpResponseUtil.sendErrorResponse(response,
                        HttpStatus.UNAUTHORIZED,
                        "로그아웃된 토큰입니다.");
            }

            // JWT 토큰에서 사용자 email 추출
            String subject = jwtUtil.getEmail(accessToken);

            // 사용자 email를 HTTP 요청 헤더에 추가하여 전달
            ServerHttpRequest newRequest = request.mutate()
                    .header("email", subject)
                    .build();

            return chain.filter(exchange.mutate().request(newRequest).build());
        };
    }

    // Mono(단일 값), Flux(다중 값) -> Spring WebFlux
    private Mono<Void> onError(ServerWebExchange exchange, String errorMsg) {
        log.error(errorMsg);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorMsg.getBytes())))
                .then(Mono.fromRunnable(() -> exchange.getResponse().setComplete()));
    }

}