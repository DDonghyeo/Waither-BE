//package com.waither.userservice.kafka;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class KafkaConsumer {
//
//    // @KafkaListener 통해 topic, group 구독
//    @KafkaListener(topics = "${spring.kafka.template.topic}", groupId = "${spring.kafka.consumer.group-id}")
//    public void consume(KafkaMessage message) {
//        log.info("[*] Kafka Consumer(Object) Message : {} ", message);
//    }
//
//    // @KafkaListener(topics = "${spring.kafka.template.default-topic}", groupId = "${spring.kafka.consumer.group-id}")
//    // public void consume(String message) {
//    // 	log.info("[*] Kafka Consumer(String) Message : {} ", message);
//    // }
//}
//
