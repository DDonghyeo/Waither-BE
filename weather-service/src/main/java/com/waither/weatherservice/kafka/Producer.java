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

	private final KafkaTemplate<String, DailyWeatherKafkaMessage> dailyWeatherKafkaTemplate;

	@Value("${spring.kafka.template.default-topic}")
	private String topic;

	public void dailyWeatherProduceMessage(DailyWeatherKafkaMessage message) {
		log.info("=================== Topic : {} ===================", topic);
		log.info("[*] Producer Message : {}", message);
		dailyWeatherKafkaTemplate.send(topic, message);
	}
}
