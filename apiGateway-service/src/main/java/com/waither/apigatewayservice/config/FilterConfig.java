package com.waither.apigatewayservice.config;

import com.waither.apigatewayservice.filter.AuthorizationHeaderFilter;
import com.waither.apigatewayservice.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// yml 방식 쓰기로 결정
//@Configuration
public class FilterConfig {

//    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {

        GlobalFilter globalFilter = new GlobalFilter();

        return builder.routes()
                .route("user", r -> r
                        .path("/user/login") //Path 확인
                        .filters(f -> f
                                .filter(globalFilter.apply(new GlobalFilter.Config()))
                                // "/user/aaa" -> "/aaa"만 전달
                                .rewritePath("/user/(?<segment>.*)", "/${segment}")
                        ) // 필터 적용
                        .uri("lb://USER-SERVICE")) // uri로 이동
    // LoadBalancing Router 설정 다른 서비스에서도 해주세요!
//
//                .route(r -> r.path("/app/**")
//                        .filters(f -> f.addRequestHeader("App Request Header", "App Service Request")
//                                .addResponseHeader("App Response Header", "App Service Response")
//                        ) //필터 적용
//                        .uri("lb://APP-SERVICE")
//                )
                .build();

    }
}