package com.waither.notiservice.utils;

import com.waither.notiservice.api.request.LocationDto;
import com.waither.notiservice.global.exception.CustomException;
import com.waither.notiservice.global.response.ApiResponse;
import com.waither.notiservice.global.response.ErrorCode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GpsTransfer {

    public static String transferToRegion(LocationDto locationDto) {
        String WEATHER_SERVICE_URL = "localhost";

        ParameterizedTypeReference<ApiResponse<String>> responseType
                = new ParameterizedTypeReference<ApiResponse<String>>() {};

        ApiResponse<String> response =
                WebClient.create(WEATHER_SERVICE_URL).get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .path("/weather/region")
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR_500)))
                .bodyToMono(responseType)
                .block();
        return response.getResult();
    }
}
