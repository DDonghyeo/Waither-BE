package com.waither.notiservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    /**
     * <h2>userMedian 동기화 토픽</h2>
     * @Producer : user-service
     * @Consumer : noti-service
     * @MessageObject : {@link com.waither.notiservice.dto.kafka.UserMedianDto}
     * @Description : noti-service의 userMedian 테이블의 데이터를 동기화 하기 위해 사용합니다.
     * 계절은 자동으로 계산합니다.
     * <br>
     * <br>
     * ‼️️️️️️️️️️️️ 가중치(weight)와 평균값이 계산된 값을 넘겨야 합니다. ‼️
     */
    @Bean
    public NewTopic userMedianTopic(){
        return TopicBuilder.name("user-median")
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * <h2>Firebase Token 동기화 토픽</h2>
     * @Producer : user-service
     * @Consumer : noti-service
     * @MessageObject : {@link com.waither.notiservice.dto.kafka.TokenDto}
     * @Description : noti-service의 firebase 토큰을 저장을 위해 사용됩니다.
     *
     */
    @Bean
    public NewTopic fireBaseTokenTopic(){
        return TopicBuilder.name("firebase-token")
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * <h2>User Settings 동기화 토픽</h2>
     * @Producer : user-service
     * @Consumer : noti-service
     * @MessageObject : {@link com.waither.notiservice.dto.kafka.UserSettingsDto}
     * @Description : noti-service의 User Data 데이터 동기화를 위해 사용됩니다.
     *
     */
    @Bean
    public NewTopic userSettingsTopic(){
        return TopicBuilder.name("user-settings")
                .partitions(1)
                .replicas(1)
                .build();
    }


    /**
     * <h2>바람 세기 알림 토픽</h2>
     * @Producer : weather-service
     * @Consumer : noti-service
     * @MessageFormat : "{바람 세기}"
     * @example : "14.2"
     * @Description : 매 주기 바람 세기를 메세지에 담아 발행합니다.
     */
    @Bean
    public NewTopic WindAlarmTopic(){
        return TopicBuilder.name("alarm-wind")
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * <h2>강설 정보 알림 토픽</h2>
     * @Producer : weather-service
     * @Consumer : noti-service
     * @MessageFormat : "{강수량}" (확인 필요)
     * @example : "0.03"
     * @Description : 매 주기 강설 정보를 메세지에 담아 발행합니다.
     */
    @Bean
    public NewTopic SnowAlarmTopic(){
        return TopicBuilder.name("alarm-snow")
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * <h2>기상 특보 알림 토픽</h2>
     * @Producer : weather-service
     * @Consumer : noti-service
     * @MessageFormat : "{기상 특보 메세지}"
     * @example : "서울 전역 강풍 특보 발령"
     * @Description : 기상 특보 메세지를 실시간으로 담아 발행합니다.
     */
    @Bean
    public NewTopic climateAlarmTopic(){
        return TopicBuilder.name("alarm-climate")
                .partitions(1)
                .replicas(1)
                .build();
    }


}
