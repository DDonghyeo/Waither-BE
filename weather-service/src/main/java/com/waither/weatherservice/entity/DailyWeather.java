package com.waither.weatherservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@RedisHash(value = "DailyWeather", timeToLive = 28800L) // 유효시간: 8시간
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
