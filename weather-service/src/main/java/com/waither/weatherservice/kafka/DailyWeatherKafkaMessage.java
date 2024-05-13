package com.waither.weatherservice.kafka;

import com.waither.weatherservice.entity.DailyWeather;

import lombok.Builder;

// 우선 문자열로 바람 세기만. 추후 변경 가능성
@Builder
public record DailyWeatherKafkaMessage(
	String pop,
	String tempMin,
	String tempMax,
	String humidity,
	String windVector,
	String windDegree
) {

	public static DailyWeatherKafkaMessage from(DailyWeather dailyWeather) {
		return DailyWeatherKafkaMessage.builder()
			.pop(dailyWeather.getPop())
			.tempMin(dailyWeather.getTempMin())
			.tempMax(dailyWeather.getTempMax())
			.humidity(dailyWeather.getHumidity())
			.windVector(dailyWeather.getWindVector())
			.windDegree(dailyWeather.getWindDegree())
			.build();
	}
}
