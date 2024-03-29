package com.waither.weatherservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@RedisHash(value = "DailyWeather", timeToLive = 28800L)
public class DailyWeather {

	@Id
	private String key;

	// 강수확률 (%)
	private String pop;
	private String tempMin;
	private String tempMax;
	private String humidity;
	private String windVector;
	private String windDegree;

	@Override
	public String toString() {
		return "DailyWeather{" +
			"pop='" + pop + '\'' +
			", tempMin='" + tempMin + '\'' +
			", tempMax='" + tempMax + '\'' +
			", humidity='" + humidity + '\'' +
			", windVector='" + windVector + '\'' +
			", windDegree='" + windDegree + '\'' +
			'}';
	}
}
