package com.waither.notiservice.config;

import com.waither.notiservice.dto.kafka.KafkaDto;
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
    public ConcurrentKafkaListenerContainerFactory<String, KafkaDto.UserMedianDto> userMedianDtoConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, KafkaDto.UserMedianDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userMedianConsumerFactory());
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

    private ConsumerFactory<String, KafkaDto.UserMedianDto> userMedianConsumerFactory() {
        Map<String, Object> props = dtoSettings();
         return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), createJsonDeserializer(KafkaDto.UserMedianDto.class));
    }


    @Bean("userSettingsKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, KafkaDto.UserSettingsDto> userSettingsConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, KafkaDto.UserSettingsDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userSettingsConsumerFactory());
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

    private ConsumerFactory<String, KafkaDto.UserSettingsDto> userSettingsConsumerFactory() {
        Map<String, Object> props = dtoSettings();
         return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), createJsonDeserializer(KafkaDto.UserSettingsDto.class));
    }


    @Bean("initialDataKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, KafkaDto.InitialDataDto> initialDataConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, KafkaDto.InitialDataDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(initialDataConsumerFactory());
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

    private ConsumerFactory<String, KafkaDto.InitialDataDto> initialDataConsumerFactory() {
        Map<String, Object> props = dtoSettings();
         return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), createJsonDeserializer(KafkaDto.InitialDataDto.class));
    }

    @Bean("weatherKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, KafkaDto.WeatherDto> weatherConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, KafkaDto.WeatherDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(weatherConsumerFactory());
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

    private ConsumerFactory<String, KafkaDto.WeatherDto> weatherConsumerFactory() {
        Map<String, Object> props = dtoSettings();
        props.put(JsonDeserializer.TYPE_MAPPINGS, "KafkaMessage:com.waither.notiservice.dto.kafka.KafkaDto.WeatherDto");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), createJsonDeserializer(KafkaDto.WeatherDto.class));
    }

    private <T> JsonDeserializer<T> createJsonDeserializer(Class<T> valueType) {
        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(valueType);
        jsonDeserializer.addTrustedPackages("com.waither.*");
        return jsonDeserializer;
    }

}
