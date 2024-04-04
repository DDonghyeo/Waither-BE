package com.waither.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    public static class Config {
        //Configuration 정보
    }

    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) -> {
            //Global PRE Filter Start ===========================================================
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("Global PRE Filter : request id : {}", request.getId());
            //Global PRE Filter End =============================================================

            //Global POST Filter Start ==========================================================
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                        //Mono : WebFlux 비동기 방식 서버 단일값 전달
                        log.info("Global POST Filter : Response Code : {}",
                                response.getStatusCode());
                    })
            //Global POST Filter End =============================================================
            );
        });
    }
}

