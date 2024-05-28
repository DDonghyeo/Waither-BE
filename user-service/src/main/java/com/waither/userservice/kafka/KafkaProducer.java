package com.waither.userservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void produceMessage(String topic, T message) {
        long startTime = System.currentTimeMillis();

        log.info("[*] Sending message to topic: {}", topic);
        log.info("[*] Message content: {}", message);

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


