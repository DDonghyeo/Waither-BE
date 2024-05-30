package com.waither.weatherservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 테스트용 Consumer
@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {

	@KafkaListener(topics = "${spring.kafka.template.topic}", groupId = "${spring.kafka.consumer.group-id}")
	public void consume(KafkaMessage message) {
		log.info("[*] Consumer Message {} ", message);
	}
}
