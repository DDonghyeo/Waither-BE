package com.waither.userservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaProducer kafkaProducer;

    @Value("${spring.kafka.template.initial-data-topic}")
    private String initialDataTopic;

    @Value("${spring.kafka.template.user-settings-topic}")
    private String userSettingTopic;

    @Value("${spring.kafka.template.user-median-topic}")
    private String userMedianTopic;

    public void sendInitialData(KafkaDto.InitialDataDto initialDataDto) {
        kafkaProducer.produceMessage(initialDataTopic, initialDataDto);
    }

    public void sendUserSettings(KafkaDto.UserSettingsDto userSettingsDto) {
        kafkaProducer.produceMessage(userSettingTopic, userSettingsDto);
    }

    public void sendUserMedian(KafkaDto.UserMedianDto userMedianDto) {
        kafkaProducer.produceMessage(userMedianTopic, userMedianDto);
    }
}