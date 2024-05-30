package com.waither.weatherservice.kafka;

import lombok.Builder;

// 우선 문자열로 바람 세기만. 추후 변경 가능성
@Builder
public record KafkaMessage(
	String region,
	String message
) {

	public static KafkaMessage of(String region, String message) {
		return KafkaMessage.builder()
			.region(region)
			.message(message)
			.build();
	}
}
