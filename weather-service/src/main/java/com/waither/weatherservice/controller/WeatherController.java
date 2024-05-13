package com.waither.weatherservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waither.weatherservice.dto.request.GetWeatherRequest;
import com.waither.weatherservice.dto.response.MainWeatherResponse;
import com.waither.weatherservice.response.ApiResponse;
import com.waither.weatherservice.service.WeatherService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

	private final WeatherService weatherService;

	@PostMapping("/main")
	public ApiResponse<MainWeatherResponse> getMainWeather(@RequestBody GetWeatherRequest getWeatherRequest) {
		return ApiResponse.onSuccess(
			weatherService.getMainWeather(getWeatherRequest.latitude(), getWeatherRequest.longitude()));
	}
}
