package com.waither.notiservice.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.kafka.test.assertj.KafkaConditions.*;


@SpringBootTest //Spring Boot 애플리케이션 테스트. Application Context를 로드해주고, 테스트 환경에서 Bean을 주입받을 수 있다.
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig // Embedded Broker가 Test Application Context에 추가됨. -> Broker를 Autowire 해줄 수 있음.
@EmbeddedKafka
public class KafkaServiceTest {

    @BeforeEach //테스트 전 처리
    void setUp() {}

    @AfterAll //테스트 후 처리
    static void afterAll() {}

    private static final String PUBLISH_TOPIC = "publish-topic"; //토픽 이름 설정
    private static final String PERFORMANCE_TOPIC = "performance-topic"; //토픽 이름 설정

    private static Instant startInstant;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;


    @Test
    @DisplayName("Kafka 메세지 발행 테스트")
    public void messagePublishingTest() throws Exception {

        //Given
        /**
         * Consumer Settings
         */
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("publish-group", "false",
                embeddedKafka);
        DefaultKafkaConsumerFactory<Integer, String> cf =
                new DefaultKafkaConsumerFactory<>(consumerProps);


        /**
         * Container & Listener Settings
         */
        ContainerProperties containerProperties = new ContainerProperties(PUBLISH_TOPIC);
        KafkaMessageListenerContainer<Integer, String> container =
                new KafkaMessageListenerContainer<>(cf, containerProperties);
        final BlockingQueue<ConsumerRecord<Integer, String>> records = new LinkedBlockingQueue<>();
        container.setupMessageListener(new MessageListener<Integer, String>() {

            @Override //Listener 설정
            public void onMessage(ConsumerRecord<Integer, String> record) {
                System.out.println("[ Kafka Publish Test ] Message received : " + record.value());
                records.add(record);
            }

        });
        container.setBeanName("templateTests");
        container.start();
        ContainerTestUtils.waitForAssignment(container,
                embeddedKafka.getPartitionsPerTopic());


        /**
         * Producer Settings
         */
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        ProducerFactory<Integer, String> pf = new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);
        template.setDefaultTopic(PUBLISH_TOPIC);


        //when 1 - test message 발행
        System.out.println("[ Kafka Publish Test ] Message Publishing (1)");
        template.sendDefault("test message");

        //then 1 - test message가 있는지
        ConsumerRecord<Integer, String> received = records.poll(10, TimeUnit.SECONDS); //메세지 꺼내오기
        assertThat(received).has(value("test message")); //발행한 메세지가 있는지?

        //when 2 - test message 2 발행
        System.out.println("[ Kafka Publish Test ] Message Publishing (2)");
        template.sendDefault(0, 2, "test message 2");

        //then 2 - test message 2 를 보유중인지
        ConsumerRecord<Integer, String> secondReceived = records.poll(10, TimeUnit.SECONDS);
        assertThat(secondReceived).has(key(2)); //key가 있는지?
        assertThat(secondReceived).has(value("test message 2")); //발행한 메세지가 있는지?
        assertThat(secondReceived).has(partition(0)); //partition이 있는지?

    }


    @Test
    @DisplayName("Kafka 성능 테스트")
    public void messagePerformanceTest() throws Exception {

        //Given
        /**
         * Consumer Settings
         */
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("performance-group", "false",
                embeddedKafka);
        DefaultKafkaConsumerFactory<Integer, String> cf =
                new DefaultKafkaConsumerFactory<>(consumerProps);
        ContainerProperties containerProperties = new ContainerProperties(PERFORMANCE_TOPIC);


        /**
         * Listener Settings
         */
        KafkaMessageListenerContainer<Integer, String> container =
                new KafkaMessageListenerContainer<>(cf, containerProperties);
        container.setupMessageListener(new MessageListener<Integer, String>() {

            @Override
            public void onMessage(ConsumerRecord<Integer, String> record) {
                Instant now = Instant.now();
                System.out.println("[ Kafka Performance Test ] " + record.value() +" Time Passed : " + Duration.between(startInstant, now).toMillis() + "ms");
            }

        });
        container.setBeanName("templateTests");
        container.start();
        ContainerTestUtils.waitForAssignment(container,
                embeddedKafka.getPartitionsPerTopic());

        /**
         * Producer Settings
         */
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        ProducerFactory<Integer, String> pf = new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);
        template.setDefaultTopic(PERFORMANCE_TOPIC);

        //when test message 발행
        System.out.println("[ Kafka Performance Test ] Message Publishing start...");
        startInstant = Instant.now(); //현재 기록
        for (int i = 0; i < 100; i++) {
            sendMessage(template, "Performance Test Message "+i);
        }

    }

    @Async
    void sendMessage(KafkaTemplate<Integer, String> template, String message) {
        template.sendDefault(0, 2, message);
    }

}