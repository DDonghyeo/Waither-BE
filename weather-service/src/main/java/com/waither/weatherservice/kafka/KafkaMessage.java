package com.waither.weatherservice.kafka;

import com.waither.weatherservice.entity.DailyWeather;

import lombok.Builder;

// 우선 문자열로 바람 세기만. 추후 변경 가능성
@Builder
public record KafkaMessage(
	String region,
	String wind
) {

	public static KafkaMessage of(DailyWeather dailyWeather, String region) {
		return KafkaMessage.builder()
			.region(region)
			.wind(dailyWeather.getWindDegree())
			.build();
	}
}
