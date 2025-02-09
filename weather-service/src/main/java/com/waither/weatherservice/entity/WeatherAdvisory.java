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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RedisHash(value = "WeatherAdvisory", timeToLive = 172800L) // 유효시간: 48시간
public class WeatherAdvisory {

	@Id
	private String id;
	private String message;

	public String toString() {
		return "WeatherAdvisory{" +
			"message='" + message + '\'' +
			'}';
	}
}
