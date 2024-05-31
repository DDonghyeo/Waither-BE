package com.waither.notiservice.service;

import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.domain.type.Season;
import com.waither.notiservice.dto.kafka.KafkaDto;
import com.waither.notiservice.global.exception.CustomException;
import com.waither.notiservice.global.response.ErrorCode;
import com.waither.notiservice.repository.jpa.UserDataRepository;
import com.waither.notiservice.repository.jpa.UserMedianRepository;
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
    @KafkaListener(topics = "${spring.kafka.template.user-median-topic}", containerFactory = "userMedianKafkaListenerContainerFactory")
    public void consumeUserMedian(KafkaDto.UserMedianDto userMedianDto) {

        Season currentSeason = TemperatureUtils.getCurrentSeason();
        log.info("[ Kafka Listener ] 사용자 중앙값 데이터 동기화");
        log.info("[ Kafka Listener ] Season : -- {} ", currentSeason.name());
        log.info("[ Kafka Listener ] Email : --> {}", userMedianDto.email());


        Optional<UserMedian> optionalUserMedian = userMedianRepository.findByEmailAndSeason(userMedianDto.email(), currentSeason);
        if (optionalUserMedian.isPresent()) {
            //User Median 이미 있을 경우
            UserMedian userMedian = optionalUserMedian.get();
            userMedian.setLevel(userMedianDto);
            userMedianRepository.save(userMedian);

        } else {
            //User Median 없을 경우 생성
            //TODO : 계절당 초기값 받아야 함
            UserMedian newUserMedian = UserMedian.builder()
                    .email(userMedianDto.email())
                    .season(currentSeason)
                    .build();
            newUserMedian.setLevel(userMedianDto);
            userMedianRepository.save(newUserMedian);
        }
    }


    /**
     * Firebase Token Listener
     * */
    @KafkaListener(topics = "firebase-token", containerFactory = "firebaseTokenKafkaListenerContainerFactory")
    public void consumeFirebaseToken(KafkaDto.TokenDto tokenDto) {

        log.info("[ Kafka Listener ] Firebase Token 동기화");
        log.info("[ Kafka Listener ] Email : --> {}", tokenDto.email());
        log.info("[ Kafka Listener ] Token : --> {}", tokenDto.token());

        //토큰 Redis 저장
        redisUtils.save(tokenDto.email(), tokenDto.token());
    }


    /**
     * User Settings Listener
     * */
    @KafkaListener(topics = "${spring.kafka.template.user-settings-topic}", containerFactory = "userSettingsKafkaListenerContainerFactory")
    public void consumeUserSettings(KafkaDto.UserSettingsDto userSettingsDto) {

        log.info("[ Kafka Listener ] 사용자 설정값 데이터 동기화");
        log.info("[ Kafka Listener ] Email : --> {}", userSettingsDto.email());
        log.info("[ Kafka Listener ] Key : --> {}", userSettingsDto.key());
        log.info("[ Kafka Listener ] Value : --> {}", userSettingsDto.value());

        Optional<UserData> userData = userDataRepository.findByEmail(userSettingsDto.email());
        if (userData.isPresent()) {
            userData.get().updateValue(userSettingsDto.key(), userSettingsDto.value());
            userDataRepository.save(userData.get());
        } else {
            log.warn("[ Kafka Listener ] User Data 초기값이 없었습니다.");
            UserData newUserData = UserData.builder()
                    .email(userSettingsDto.email())
                    .build();
            newUserData.updateValue(userSettingsDto.key(), userSettingsDto.value());
            userDataRepository.save(newUserData);
        }

    }

    @KafkaListener(topics = "${spring.kafka.template.initial-data-topic}", containerFactory = "initialDataKafkaListenerContainerFactory")
    public void consumeUserInit(KafkaDto.InitialDataDto initialDataDto) {

        log.info("[ Kafka Listener ] 초기 설정값 세팅");
        log.info("[ Kafka Listener ] email --> {}", initialDataDto.email());
        userDataRepository.save(initialDataDto.toUserDataEntity());
        userMedianRepository.save(initialDataDto.toUserMedianEntity());
    }




    /**
     * 바람 세기 알림 Listener
     * */
    @KafkaListener(topics = "alarm-wind")
    public void consumeWindAlarm(@Payload String message) {
        StringBuilder sb = new StringBuilder();

        Long windStrength = Long.valueOf(message); //바람세기

        log.info("[ Kafka Listener ] 바람 세기");
        log.info("[ Kafka Listener ] Wind Strength : --> {}", windStrength);

        //TODO : 알림 보낼 사용자 정보 가져오기 (Redis)
        List<Long> userIds = new ArrayList<>();

        //TODO : 바람 세기 알림 멘트 정리
        sb.append("현재 바람 세기가 ").append(windStrength).append("m/s 이상입니다. 강풍에 주의하세요.");

        System.out.println("[ 푸시알림 ] 바람 세기 알림");
        sendAlarms(userIds, sb.toString());
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

        System.out.println("[ 푸시알림 ] 강수량 알림");
        sendAlarms(userIds, resultMessage);
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
        System.out.println("[ 푸시알림 ] 기상 특보 알림");
        sendAlarms(userIds, resultMessage);
    }


    private void sendAlarms(List<Long> userEmails, String message) {
        userEmails.forEach(email ->{
            String token = String.valueOf(redisUtils.get(String.valueOf(email)));
            if (token == null) { //token을 찾지 못했을 경우
                throw new CustomException(ErrorCode.FIREBASE_TOKEN_NOT_FOUND);
            }


            System.out.printf("[ 푸시알림 ] Email ---> {%d}", email);
            System.out.printf("[ 푸시알림 ] message ---> {%s}", message);
        });
    }

}
