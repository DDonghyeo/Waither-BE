package com.waither.weatherservice.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Producer {

	private final KafkaTemplate<String, String> kafkaTemplate;

	@Value("${spring.kafka.template.default-topic}")
	private String topic;

	public void produceMessage(String message) {
		log.info("=================== Topic : {} ===================", topic);
		log.info("[*] Producer Message : {}", message);
		kafkaTemplate.send(topic, message);
	}
}
