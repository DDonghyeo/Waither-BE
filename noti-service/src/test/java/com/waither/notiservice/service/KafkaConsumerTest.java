package com.waither.notiservice.service;

import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.dto.kafka.TokenDto;
import com.waither.notiservice.dto.kafka.UserMedianDto;
import com.waither.notiservice.dto.kafka.UserSettingsDto;
import com.waither.notiservice.redis.FireTokenBaseRepository;
import com.waither.notiservice.repository.UserDataRepository;
import com.waither.notiservice.repository.UserMedianRepository;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig
public class KafkaConsumerTest {

    Map<String, Object> jsonProps;
    Map<String, Object> stringProps;

    @BeforeEach
    void setUp() {
        jsonProps = new HashMap<>();
        jsonProps.put(ProducerConfig.ACKS_CONFIG, "all");
        jsonProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        jsonProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        stringProps = jsonProps;
        jsonProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        stringProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    }

    @BeforeAll
    static void beforeAll() {

    }

    @Autowired
    private UserMedianRepository userMedianRepository;

    @Autowired
    private FireTokenBaseRepository fireTokenBaseRepository;

    @Autowired
    private UserDataRepository userDataRepository;

    @Test
    @DisplayName("User Median Consumer Test")
    @Transactional //Transaction 후 Rollback (작동하지 않음. Listener 로 작동해서?)
    void userMedianTest() throws InterruptedException {
        //Given
        ProducerFactory<Integer, UserMedianDto> pf = new DefaultKafkaProducerFactory<>(jsonProps);
        KafkaTemplate<Integer, UserMedianDto> template = new KafkaTemplate<>(pf);

        //when
        UserMedianDto userMedianDto = UserMedianDto.builder()
                .userId(0L)
                .level(1)
                .temperature(10.5)
                .build();
        CompletableFuture<SendResult<Integer, UserMedianDto>> future = template.send("user-median", userMedianDto);

        //then
        future.whenComplete(((result, throwable) -> {
            assertThat(throwable).isNotNull();
            System.out.println("[ Kafka Test ] Publish Complete - user-median");
            System.out.println("offset : "+ result.getRecordMetadata().offset());
        }
        ));
        Thread.sleep(2000); //2초 대기

        userMedianRepository.findAll().forEach(userMedian -> {
            System.out.println(" userId : " + userMedian.getUserId());
        });

        assertThat(userMedianRepository.findById(0L).get().getMedianOf1And2()).isEqualTo(10.5);

        //끝나고 삭제 -> Rollback 일어나지 않아서
        userMedianRepository.deleteById(0L);
    }



    @Test
    @DisplayName("Fire Base Consumer Test")
    @Transactional //Transaction 후 Rollback (작동하지 않음. Listener 로 작동해서?)
    void firebaseTokenTest() throws InterruptedException {
        //Given
        ProducerFactory<Integer, TokenDto> pf = new DefaultKafkaProducerFactory<>(jsonProps);
        KafkaTemplate<Integer, TokenDto> template = new KafkaTemplate<>(pf);

        //when
        TokenDto tokenDto = TokenDto.builder()
                .userId(0L)
                .token("test token")
                .build();
        CompletableFuture<SendResult<Integer, TokenDto>> future = template.send("firebase-token", tokenDto);

        //then
        future.whenComplete(((result, throwable) -> {
            assertThat(throwable).isNotNull();
            System.out.println("[ Kafka Test ] Publish Complete - firebase-token");
            System.out.println("offset : "+ result.getRecordMetadata().offset());
        }
        ));
        Thread.sleep(2000); //2초 대기


        assertThat(fireTokenBaseRepository.findById(0L).get().getToken()).isEqualTo("test token");

        //끝나고 삭제 -> Rollback 일어나지 않아서
        fireTokenBaseRepository.deleteById(0L);
    }

    @Test
    @DisplayName("User Settings Consumer Test")
    @Transactional //Transaction 후 Rollback (작동하지 않음. Listener 로 작동해서?)
    void userSettingsWindDegreeTest() throws InterruptedException {
        //Given
        ProducerFactory<Integer, UserSettingsDto> pf = new DefaultKafkaProducerFactory<>(jsonProps);
        KafkaTemplate<Integer, UserSettingsDto> template = new KafkaTemplate<>(pf);

        //when
        userDataRepository.save(UserData.builder()
                .windDegree(10)
                .userId(0L)
                .build());
        UserSettingsDto userSettingsDto = UserSettingsDto.builder()
                .userId(0L)
                .key("windDegree")
                .value("11")
                .build();

        CompletableFuture<SendResult<Integer, UserSettingsDto>> future = template.send("user-settings", userSettingsDto);

        //then
        future.whenComplete(((result, throwable) -> {
            assertThat(throwable).isNotNull();
            System.out.println("[ Kafka Test ] Publish Complete - user-settings");
            System.out.println("offset : "+ result.getRecordMetadata().offset());
        }
        ));
        Thread.sleep(2000); //2초 대기


        assertThat(userDataRepository.findById(0L).get().getWindDegree()).isEqualTo(11);

        //끝나고 삭제 -> Rollback 일어나지 않아서
        userDataRepository.deleteById(0L);
    }

    @Test
    @DisplayName("Alarm Wind Consumer Test")
    void alarmWindTest() throws InterruptedException {
        //Given
        ProducerFactory<Integer, String> pf = new DefaultKafkaProducerFactory<>(jsonProps);
        KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);

        //when
        CompletableFuture<SendResult<Integer, String>> future = template.send("alarm-wind", "15");

        //then
        future.whenComplete(((result, throwable) -> {
            assertThat(throwable).isNotNull();
            System.out.println("[ Kafka Test ] Publish Complete - alarm-wind");
            System.out.println("offset : "+ result.getRecordMetadata().offset());
        }
        ));
        Thread.sleep(2000); //2초 대기


        //TODO : 서비스 정리
    }

    @Test
    @DisplayName("Alarm Snow Consumer Test")
    void alarmSnowTest() throws InterruptedException {
        //Given
        ProducerFactory<Integer, String> pf = new DefaultKafkaProducerFactory<>(jsonProps);
        KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);

        //when
        CompletableFuture<SendResult<Integer, String>> future = template.send("alarm-snow", "0.4");

        //then
        future.whenComplete(((result, throwable) -> {
            assertThat(throwable).isNotNull();
            System.out.println("[ Kafka Test ] Publish Complete - snow-alarm");
            System.out.println("offset : "+ result.getRecordMetadata().offset());
        }
        ));
        Thread.sleep(2000); //2초 대기


        //TODO : 서비스 정리
    }


    @Test
    @DisplayName("Alarm Climate Consumer Test")
    void alarmClimateTest() throws InterruptedException {
        //Given
        ProducerFactory<Integer, String> pf = new DefaultKafkaProducerFactory<>(jsonProps);
        KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);

        //when
        CompletableFuture<SendResult<Integer, String>> future = template.send("alarm-climate", "기상 특보 테스트");

        //then
        future.whenComplete(((result, throwable) -> {
            assertThat(throwable).isNotNull();
            System.out.println("[ Kafka Test ] Publish Complete - alarm-climate");
            System.out.println("offset : "+ result.getRecordMetadata().offset());
        }
        ));
        Thread.sleep(2000); //2초 대기


        //TODO : 서비스 정리
    }





}
