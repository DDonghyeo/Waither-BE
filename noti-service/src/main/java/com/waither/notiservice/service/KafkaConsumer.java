package com.waither.notiservice.service;

import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.domain.redis.NotificationRecord;
import com.waither.notiservice.enums.Season;
import com.waither.notiservice.dto.kafka.KafkaDto;
import com.waither.notiservice.repository.jpa.UserDataRepository;
import com.waither.notiservice.repository.jpa.UserMedianRepository;
import com.waither.notiservice.repository.redis.NotificationRecordRepository;
import com.waither.notiservice.utils.RedisUtils;
import com.waither.notiservice.utils.TemperatureUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final AlarmService alarmService;
    private final UserDataRepository userDataRepository;
    private final UserMedianRepository userMedianRepository;
    private final NotificationRecordRepository notificationRecordRepository;
    private final RedisUtils redisUtils;

    /**
     * 중앙값 동기화 Listener
     * */
    @Transactional
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
            log.warn("[ Kafka Listener ] User Median 초기값이 없었습니다.");
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
    @Transactional
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
    @Transactional
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

    @Transactional
    @KafkaListener(topics = "${spring.kafka.template.initial-data-topic}", containerFactory = "initialDataKafkaListenerContainerFactory")
    public void consumeUserInit(KafkaDto.InitialDataDto initialDataDto) {

        log.info("[ Kafka Listener ] 초기 설정값 세팅");
        log.info("[ Kafka Listener ] email --> {}", initialDataDto.email());
        userDataRepository.save(initialDataDto.toUserDataEntity());
        userMedianRepository.saveAll(initialDataDto.toUserMedianList());
    }




    /**
     * 바람 세기 알림 Listener
     * @Query  : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300  */
    @Transactional
    @KafkaListener(topics = "alarm-wind", containerFactory = "weatherKafkaListenerContainerFactory")
    public void consumeWindAlarm(KafkaDto.WeatherDto weatherDto) {

        int currentHour = LocalDateTime.now().getHour();
        // 22:00 ~ 07:00 는 알림을 전송하지 않음
        if (currentHour >= 22 || currentHour <= 7) {
            return;
        }

        String title = "Waither 바람 세기 알림";
        StringBuilder sb = new StringBuilder();

        String region = weatherDto.region();
        Double windStrength = Double.valueOf(weatherDto.message()); //바람세기

        log.info("[ Kafka Listener ] 바람 세기");
        log.info("[ Kafka Listener ] Wind Strength : --> {}", windStrength);

        // Wind Alert를 True로 설정한 User Query
        List<UserData> userData = userDataRepository.findAllByWindAlertIsTrue();

        //알림 보낼 사용자 이메일
        List<String> userEmails = filterRegionAndWindAlarm(region, userData, currentHour);

        sb.append("현재 바람 세기가 ").append(windStrength).append("m/s 이상입니다.");

        System.out.println("[ 푸시알림 ] 바람 세기 알림");

        alarmService.sendAlarms(userEmails,title, sb.toString());

        //Record 알림 시간 초기화
        userEmails
                .forEach(email -> {
                    Optional<NotificationRecord> notificationRecord = notificationRecordRepository.findByEmail(email);
                    notificationRecord.ifPresent(NotificationRecord::initializeWindTime);
                });
    }


    /**
     * 강설 정보 알림 Listener <br>
     * 기상청 기준 <br>
     * 약한 비     1~3mm <br>
     * 보통 비     3~15mm <br>
     * 강한 비     15~30mm <br>
     * 매우 강한 비 30mm 이상 <br>
     * <a href="https://www.kma.go.kr/kma/biz/forecast05.jsp">참고</a>
     */
    @Transactional
    @KafkaListener(topics = "alarm-snow", containerFactory = "weatherKafkaListenerContainerFactory")
    public void consumeSnow(KafkaDto.WeatherDto weatherDto) {

        int currentHour = LocalDateTime.now().getHour();
        // 22:00 ~ 07:00 는 알림을 전송하지 않음
        if (currentHour >= 22 || currentHour <= 7) {
            return;
        }

        String title = "Waither 강수 정보 알림";
        StringBuilder sb = new StringBuilder();
        
        String region = weatherDto.region();
        Double prediction = Double.valueOf(weatherDto.message()); //강수량

        log.info("[ Kafka Listener ] 강수량  지역 --> {}", region);
        log.info("[ Kafka Listener ] 걍수량 --> {}", prediction);

        List<UserData> userData = userDataRepository.findAllBySnowAlertIsTrue();

        //예시 : 현재 서울특별시 지역에 2mm의 약한 비가 내릴 예정입니다.
        //TODO: 언제 내리는지? 확인 필요
        sb.append("현재 ").append(region).append(" 지역에 ").append(prediction).append("mm의 ")
                .append(getExpression(prediction)).append("가 내릴 예정입니다.");

        //알림 보낼 사용자 이메일
        List<String> userEmails = filterRegionAndRainAlarm(region, userData, currentHour);


        System.out.println("[ 푸시알림 ] 강수량 알림");
        alarmService.sendAlarms(userEmails, title, sb.toString());

        //Record 알림 시간 초기화
        userEmails
                .forEach(email -> {
                    Optional<NotificationRecord> notificationRecord = notificationRecordRepository.findByEmail(email);
                    notificationRecord.ifPresent(NotificationRecord::initializeRainTime);
                });

    }

    /**
     * 기상 특보 알림 Listener
     * */
    @Transactional
    @KafkaListener(topics = "alarm-climate", containerFactory = "weatherKafkaListenerContainerFactory")
    public void consumeClimateAlarm(KafkaDto.WeatherDto weatherDto) {
        int currentHour = LocalDateTime.now().getHour();
        // 22:00 ~ 07:00 는 알림을 전송하지 않음
        if (currentHour >= 22 || currentHour <= 7) {
            return;
        }

        String title = "Waither 기상 특보 알림";
        StringBuilder sb = new StringBuilder();

        log.info("[ Kafka Listener ] 기상 특보");

        String region = weatherDto.region();
        String message = weatherDto.message();

        // Wind Climate를 True로 설정한 User Query
        List<UserData> userData = userDataRepository.findAllByClimateAlertIsTrue();

        // 알림 보낼 사용자 이메일
        List<String> userEmails = filterRegion(region, userData);

        sb.append("[기상청 기상 특보] ").append(message);

        System.out.println("[ 푸시알림 ] 기상 특보 알림");
        alarmService.sendAlarms(userEmails, title, sb.toString());
    }


    //지역 필터링 & 알림 규칙 검사
    private List<String> filterRegionAndWindAlarm(String region, List<UserData> userData, int currentHour) {
        return userData.stream()
                .filter(data -> {
                    Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(data.getEmail());
                    return notiRecord.map(notificationRecord ->
                            (Math.abs(notiRecord.get().getLastWindAlarmReceived().getHour() - currentHour) >=3
                                    && notificationRecord.getRegion().equals(region) )
                    ).orElse(false);
                })
                .map(UserData::getEmail)
                .toList();
    }

    //지역 필터링 & 알림 규칙 검사
    private List<String> filterRegionAndRainAlarm(String region, List<UserData> userData, int currentHour) {
        return userData.stream()
                .filter(data -> {
                    Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(data.getEmail());
                    return notiRecord.map(notificationRecord ->
                            (Math.abs(notiRecord.get().getLastRainAlarmReceived().getHour() - currentHour) >=3
                                    && notificationRecord.getRegion().equals(region) )
                    ).orElse(false);
                })
                .map(UserData::getEmail)
                .toList();
    }

    private List<String> filterRegion(String region, List<UserData> userData) {
        return userData.stream()
                .filter(data -> {
                    Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(data.getEmail());
                    return notiRecord.map(notificationRecord -> notificationRecord.getRegion().equals(region)).orElse(false);
                })
                .map(UserData::getEmail)
                .toList();
    }

    //강수 표현
    private String getExpression(double prediction) {
        //1~3mm : 약한 비
        if (prediction > 1 && prediction < 3) {
            return "약한 비";
            //3~15mm : 비
        } else if (prediction >=3 && prediction < 15) {
            return "비";
            //15~30mm 강한 비
        } else if (prediction >= 15 &&prediction <= 30) {
            return "강한 비";
            //30mm~ 매우 강한 비
        } else if (prediction >= 30) {
            return "매우 강한 비";
        } else return "비";
    }
}
