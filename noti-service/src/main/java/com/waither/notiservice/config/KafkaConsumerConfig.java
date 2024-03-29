package com.waither.notiservice.config;

import com.waither.notiservice.dto.kafka.TokenDto;
import com.waither.notiservice.dto.kafka.UserMedianDto;
import com.waither.notiservice.dto.kafka.UserSettingsDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "notice_1")
    private String groupId;

    @Value(value = "earliest")
    private String autoOffsetResetStrategy;

    @Value(value = "false")
    private String enableAutoCommit;

    @Value(value = "5000")
    private String autoCommitIntervalMs;

    @Value(value = "30000")
    private String sessionTimeoutMs;

    @Value(value = "100")
    private String maxPollRecords;

    private Map<String, Object> defaultSettings() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMs);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetStrategy);
        return props;
    }

    private Map<String, Object> dtoSettings() {
        Map<String, Object> props = defaultSettings();
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        return props;
    }

    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> props = defaultSettings();
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.getContainerProperties().setPollTimeout(3000); // max time to block in the consumer waiting for records
        return factory;
    }


    @Bean("userMedianKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UserMedianDto> userMedianDtoConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, UserMedianDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userMedianConsumerFactory());
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

    private ConsumerFactory<String, UserMedianDto> userMedianConsumerFactory() {
        Map<String, Object> props = dtoSettings();
         return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(UserMedianDto.class));
    }



    @Bean("firebaseTokenKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, TokenDto> firebaseTokenConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, TokenDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(firebaseTokenConsumerFactory());
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

    private ConsumerFactory<String, TokenDto> firebaseTokenConsumerFactory() {
        Map<String, Object> props = dtoSettings();
         return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(TokenDto.class));
    }



    @Bean("userSettingsKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UserSettingsDto> userSettingsConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, UserSettingsDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userSettingsConsumerFactory());
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

    private ConsumerFactory<String, UserSettingsDto> userSettingsConsumerFactory() {
        Map<String, Object> props = dtoSettings();
         return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(UserSettingsDto.class));
    }




}
