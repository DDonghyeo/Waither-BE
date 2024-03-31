package com.waither.weatherservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@RedisHash(value = "DisasterMessage", timeToLive = 86400L) // 유효시간: 24시간
public class DisasterMessage {

	@Id
	private String key;
	private String message;

	public String toString() {
		return "DisasterMessage{" +
			"message='" + message + '\'' +
			'}';
	}
}
