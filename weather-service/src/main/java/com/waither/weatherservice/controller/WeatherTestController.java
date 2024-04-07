package com.waither.weatherservice.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waither.weatherservice.dto.AirTestRequest;
import com.waither.weatherservice.dto.ForeCastTestRequest;
import com.waither.weatherservice.dto.MsgTestRequest;
import com.waither.weatherservice.service.WeatherService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/weather")
public class WeatherTestController {

	private final WeatherService weatherService;

	@PostMapping("/short")
	public void createExpectedWeatherTest(@RequestBody ForeCastTestRequest request) throws URISyntaxException {
		weatherService.createExpectedWeather(request.nx(), request.ny(), request.baseDate(), request.baseTime());
	}

	@PostMapping("/daily")
	public void createDailyWeatherTest(@RequestBody ForeCastTestRequest request) throws URISyntaxException {
		weatherService.createDailyWeather(request.nx(), request.ny(), request.baseDate(), request.baseTime());
	}

	@PostMapping("/msg")
	public void createDisasterMsgTest(@RequestBody MsgTestRequest request) throws URISyntaxException, IOException {
		weatherService.createDisasterMsg(request.location());
	}

	@PostMapping("/air")
	public void airKoreaTest(@RequestBody AirTestRequest request) throws URISyntaxException, IOException {
		weatherService.createAirKorea(request.searchDate());
	}
}
