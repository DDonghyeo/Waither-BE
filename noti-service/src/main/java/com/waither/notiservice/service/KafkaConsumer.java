package com.waither.notiservice.service;

import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.domain.type.Season;
import com.waither.notiservice.dto.kafka.TokenDto;
import com.waither.notiservice.dto.kafka.UserMedianDto;
import com.waither.notiservice.dto.kafka.UserSettingsDto;
import com.waither.notiservice.repository.UserDataRepository;
import com.waither.notiservice.repository.UserMedianRepository;
import com.waither.notiservice.utils.RedisUtils;
import com.waither.notiservice.utils.TemperatureUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class KafkaConsumer {

    private final UserDataRepository userDataRepository;
    private final UserMedianRepository userMedianRepository;

    private final RedisUtils redisUtils;

    /**
     * 중앙값 동기화 Listener
     * */
    @KafkaListener(topics = "user-median", containerFactory = "userMedianKafkaListenerContainerFactory")
    public void consumeUserMedian(UserMedianDto userMedianDto) {

        Season season = TemperatureUtils.getCurrentSeason();
        log.info("[ Kafka Listener ] 사용자 중앙값 데이터 동기화");
        log.info("[ Kafka Listener ] Season : -- {} ", season.name());
        log.info("[ Kafka Listener ] User Id : --> {}", userMedianDto.getUserId());
        log.info("[ Kafka Listener ] Level : --> {}", userMedianDto.getLevel());
        log.info("[ Kafka Listener ] Temperature : --> {}", userMedianDto.getTemperature());

        Optional<UserMedian> userMedian = userMedianRepository.findById(userMedianDto.getUserId());
        if (userMedian.isPresent()) {
            userMedian.get()
                    .setLevel(userMedianDto.getLevel(), userMedianDto.getTemperature());
        } else {
            UserMedian newUserMedian = UserMedian.builder()
                    .userId(userMedianDto.getUserId())
                    .build();
            newUserMedian.setLevel(userMedianDto.getLevel(),
                    userMedianDto.getTemperature());
            userMedianRepository.save(newUserMedian);
        }


    }


    /**
     * Firebase Token Listener
     * */
    @KafkaListener(topics = "firebase-token", containerFactory = "firebaseTokenKafkaListenerContainerFactory")
    public void consumeFirebaseToken(TokenDto tokenDto) {

        log.info("[ Kafka Listener ] Firebase Token 동기화");
        log.info("[ Kafka Listener ] User Id : --> {}", tokenDto.getUserId());
        log.info("[ Kafka Listener ] Token : --> {}", tokenDto.getToken());

        //토큰 Redis 저장
        redisUtils.save(String.valueOf(tokenDto.getUserId()), tokenDto.getToken());
    }


    /**
     * User Settings Listener
     * */
    @KafkaListener(topics = "user-settings", containerFactory = "userSettingsKafkaListenerContainerFactory")
    public void consumeUserSettings(UserSettingsDto userSettingsDto) {

        log.info("[ Kafka Listener ] 사용자 설정값 데이터 동기화");
        log.info("[ Kafka Listener ] User Id : --> {}", userSettingsDto.getUserId());
        log.info("[ Kafka Listener ] Key : --> {}", userSettingsDto.getKey());
        log.info("[ Kafka Listener ] Value : --> {}", userSettingsDto.getValue());

        Optional<UserData> userData = userDataRepository.findById(userSettingsDto.getUserId());
        if (userData.isPresent()) {
            userData.get().updateValue(userSettingsDto.getKey(), userSettingsDto.getValue());
            userDataRepository.save(userData.get());
        } else {
            UserData newUserData = UserData.builder()
                    .userId(userSettingsDto.getUserId())
                    .build();
            newUserData.updateValue(userSettingsDto.getKey(), userSettingsDto.getValue());
            userDataRepository.save(newUserData);
        }

    }





    /**
     * 바람 세기 알림 Listener
     * */
    @KafkaListener(topics = "alarm-wind")
    public void consumeWindAlarm(@Payload String message) {
        String resultMessage = "";
        Long windStrength = Long.valueOf(message); //바람세기

        log.info("[ Kafka Listener ] 바람 세기");
        log.info("[ Kafka Listener ] Wind Strength : --> {}", windStrength);

        //TODO : 알림 보낼 사용자 정보 가져오기 (Redis)
        List<Long> userIds = new ArrayList<>();

        //TODO : 바람 세기 알림 멘트 정리
        resultMessage += "현재 바람 세기가 " + windStrength + "m/s 이상입니다. 강풍에 주의하세요.";

        //TODO : 푸시알림 전송
        String finalResultMessage = resultMessage;
        userIds.forEach(id ->{
            String token = String.valueOf(redisUtils.get(String.valueOf(id)));
            System.out.println("[ 푸시알림 ] 바람 세기 알림");
            System.out.printf("[ 푸시알림 ] userId ---> {%d}", id);
            System.out.printf("[ 푸시알림 ] message ---> {%s}", finalResultMessage);
        });

    }


    /**
     * 강설 정보 알림 Listener
     * */
    @KafkaListener(topics = "alarm-snow")
    public void consumeSnow(@Payload String message) {
        String resultMessage = "";
        Double snow = Double.valueOf(message); //강수량

        log.info("[ Kafka Listener ] 강수량");
        log.info("[ Kafka Listener ] Snow : --> {}", snow);

        //TODO : 알림 보낼 사용자 정보 가져오기 (Redis)
        List<Long> userIds = new ArrayList<>();

        //TODO : 알림 멘트 정리
        resultMessage += "현재 강수량 " + snow + "m/s 이상입니다.";

        //TODO : 푸시알림 전송
        String finalResultMessage = resultMessage;
        userIds.forEach(id ->{
            //TODO : 예외처리하기
            String token = String.valueOf(redisUtils.get(String.valueOf(id)));
            System.out.println("[ 푸시알림 ] 강수량 알림");
            System.out.printf("[ 푸시알림 ] userId ---> {%d}", id);
            System.out.printf("[ 푸시알림 ] message ---> {%s}", finalResultMessage);
        });

    }

    /**
     * 기상 특보 알림 Listener
     * */
    @KafkaListener(topics = "alarm-climate")
    public void consumeClimateAlarm(@Payload String message) {
        String resultMessage = "";

        log.info("[ Kafka Listener ] 기상 특보");

        //TODO : 알림 보낼 사용자 정보 가져오기 (Redis)
        List<Long> userIds = new ArrayList<>();

        resultMessage += "[기상청 기상 특보] " + message;

        //TODO : 푸시알림 전송
        String finalResultMessage = resultMessage;
        userIds.forEach(id ->{
            String token = String.valueOf(redisUtils.get(String.valueOf(id)));
            System.out.println("[ 푸시알림 ] 기상 특보 알림");
            System.out.printf("[ 푸시알림 ] userId ---> {%d}", id);
            System.out.printf("[ 푸시알림 ] message ---> {%s}", finalResultMessage);
        });

    }
}
