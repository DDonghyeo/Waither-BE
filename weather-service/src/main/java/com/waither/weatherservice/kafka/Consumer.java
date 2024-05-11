package com.waither.weatherservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {

	@KafkaListener(topics = "${spring.kafka.template.topic}", groupId = "${spring.kafka.consumer.group-id}")
	public void dailyWeatherConsume(String message) {

		log.info("Consumer Test ========================== ");
		log.info("[*] Consumer Message {} ", message);
	}
}
