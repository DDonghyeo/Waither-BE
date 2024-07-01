package com.waither.notiservice.utils;

import com.waither.notiservice.api.request.LocationDto;
import com.waither.notiservice.api.response.MainWeatherResponse;
import com.waither.notiservice.global.exception.CustomException;
import com.waither.notiservice.global.response.ApiResponse;
import com.waither.notiservice.global.response.ErrorCode;
import lombok.NoArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@NoArgsConstructor
@Component
public class RestClient {

    public static String WEATHER_SERVICE_URL = "localhost";

    public static String transferToRegion(LocationDto location) {

        ParameterizedTypeReference<ApiResponse<String>> responseType
                = new ParameterizedTypeReference<ApiResponse<String>>() {};

        ApiResponse<String> response =
                WebClient.create(WEATHER_SERVICE_URL).get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .path("/weather/region")
                        .queryParam("latitude", location.latitude())
                        .queryParam("longitude", location.longitude())
                        .port(8081)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR_500)))
                .bodyToMono(responseType)
                .block();
        return response.getResult();
    }

    public static MainWeatherResponse getMainWeather(LocationDto location) {
        ParameterizedTypeReference<ApiResponse<MainWeatherResponse>> responseType
                = new ParameterizedTypeReference<ApiResponse<MainWeatherResponse>>() {};
        return WebClient.create(WEATHER_SERVICE_URL).get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .path("/weather/main")
                        .queryParam("latitude", location.latitude())
                        .queryParam("longitude", location.longitude())
                        .port(8081)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR_500)))
                .bodyToMono(responseType)
                .block().getResult();
    }
}
