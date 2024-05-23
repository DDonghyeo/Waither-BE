package com.waither.weatherservice.scheduler;

import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;

import com.waither.weatherservice.service.WeatherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties
public class SchedulerConfig {

	private final WeatherService weatherService;
	@Scheduled(cron = "0 0 14,17,20 * * *")
	public void createDailyWeather() throws URISyntaxException {

		LocalDateTime now = LocalDateTime.now();
		String[] dateTime = weatherService.convertLocalDateTimeToString(now).split("_");
		// TODO 지역 DB 정리 후 변경 예정
		weatherService.createDailyWeather(55, 127, dateTime[0], dateTime[1]);
	}
}
