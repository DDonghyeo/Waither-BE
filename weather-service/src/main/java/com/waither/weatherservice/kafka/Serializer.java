package com.waither.weatherservice.kafka;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Serializer {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	// DailyWeatherKafkaMessage 를 JSON 문자열로 직렬화
	public static String serializeDailyWeather(DailyWeatherKafkaMessage dailyWeather) throws
		JsonProcessingException {
		return objectMapper.writeValueAsString(dailyWeather);
	}

	// JSON 문자열을 DailyWeatherKafkaMessage 로 역직렬화
	public static DailyWeatherKafkaMessage deserializeDailyWeather(String json) throws JsonProcessingException {
		return objectMapper.readValue(json, DailyWeatherKafkaMessage.class);
	}
}
