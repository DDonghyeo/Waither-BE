package com.waither.weatherservice.kafka;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Producer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${spring.kafka.template.topic}")
	private String topic;

	public void produceMessage(Object message) {
		log.info("[*] Producer Message : {}", message);
		long startTime = System.currentTimeMillis();

		kafkaTemplate.send(topic, message);

		CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);

		future.whenComplete(((result, throwable) -> {
			if (throwable == null) {
				// 해당 파티션의 offset
				log.info("[*] offset : {}", result.getRecordMetadata().offset());
				// 메시지 전송 후의 시간 기록
				long endTime = System.currentTimeMillis();
				log.info("[*] Kafka Message(Object) sent successfully in {} ms", endTime - startTime);
			} else {
				log.error("[*] fail to publish", throwable);
			}
		}));
	}
}
