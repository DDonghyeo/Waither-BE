package com.waither.weatherservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waither.weatherservice.dto.request.GetWeatherRequest;
import com.waither.weatherservice.dto.response.MainWeatherResponse;
import com.waither.weatherservice.response.ApiResponse;
import com.waither.weatherservice.service.WeatherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/weather")
public class WeatherController {

	private final WeatherService weatherService;

	@GetMapping("/main")
	public ApiResponse<MainWeatherResponse> getMainWeather(@ModelAttribute @Valid GetWeatherRequest getWeatherRequest) {
		return ApiResponse.onSuccess(
			weatherService.getMainWeather(getWeatherRequest.latitude(), getWeatherRequest.longitude()));
	}

	@GetMapping("/region")
	public ApiResponse<String> convertGpsToRegionName(@ModelAttribute @Valid GetWeatherRequest getWeatherRequest) {
		return ApiResponse.onSuccess(
			weatherService.convertGpsToRegionName(getWeatherRequest.latitude(), getWeatherRequest.longitude()));
	}
}
