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
@RedisHash(value = "DisasterMessage", timeToLive = 86400L) // 유효시간: 24시간
public class DisasterMessage {

	@Id
	private String id;
	private String message;

	public String toString() {
		return "DisasterMessage{" +
			"message='" + message + '\'' +
			'}';
	}
}
