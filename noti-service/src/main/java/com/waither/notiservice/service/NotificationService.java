package com.waither.notiservice.service;

import com.waither.notiservice.api.response.NotificationResponse;
import com.waither.notiservice.domain.Notification;
import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.api.request.LocationDto;
import com.waither.notiservice.domain.redis.NotificationRecord;
import com.waither.notiservice.enums.Season;
import com.waither.notiservice.global.exception.CustomException;
import com.waither.notiservice.global.response.ErrorCode;
import com.waither.notiservice.repository.jpa.NotificationRepository;
import com.waither.notiservice.repository.jpa.UserDataRepository;
import com.waither.notiservice.repository.jpa.UserMedianRepository;
import com.waither.notiservice.repository.redis.NotificationRecordRepository;
import com.waither.notiservice.utils.TemperatureUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserMedianRepository userMedianRepository;
    private final UserDataRepository userDataRepository;
    private final NotificationRecordRepository notificationRecordRepository;

    private final AlarmService alarmService;


    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(String email) {

        return notificationRepository.findAllByEmail(email)
                .stream().map(NotificationResponse::of).toList();
    }

    @Transactional
    public void deleteNotification(String email, String notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_404));

        if (!notification.getEmail().equals(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_401);
        }

        notificationRepository.delete(notification);

    }

    @Transactional
    public String sendGoOutAlarm(String email) {

        UserData userData = userDataRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.NO_USER_DATA_REGISTERED));

        Season currentSeason = TemperatureUtils.getCurrentSeason();

        LocalDateTime now = LocalDateTime.now();
        String title = now.getMonth() + "월 " + now.getDayOfMonth() + "일 날씨 정보입니다.";
        StringBuilder sb = new StringBuilder();

        /**
         * 1. 기본 메세지 시작 형식
         */
        //TODO : 날씨 정보 가져오기 & 날씨별 멘트 정리
        String nickName = userData.getNickName();
        sb.append(nickName).append("님, 오늘은 ");


        /**
         * 2. 당일 온도 정리 - Weather Service
         * {@link com.waither.notiservice.enums.Expressions} 참고
         */
        double temperature = 10.8;

        if (userData.isUserAlert()) {
            //사용자 맞춤 알림이 on이라면 -> 계산 후 전용 정보 제공
            UserMedian userMedian = userMedianRepository.findByEmailAndSeason(email, currentSeason).orElseThrow(
                    () ->  new CustomException(ErrorCode.NO_USER_MEDIAN_REGISTERED));

            sb.append(TemperatureUtils.createUserDataMessage(userMedian, temperature));
        } else {
            //사용자 맞춤 알림이 off라면 -> 하루 평균 온도 정보 제공
            sb.append("평균 온도가 ").append(temperature).append("도입니다.");
        }


//        /**
//         * 2. 미세먼지 정보 가져오기 - Weather Service
//         */
//        //TODO : 미세먼지 On/Off 확인, 멘트
//        finalMessage += " 미세먼지는 없습니다.";

        /**
         * 3. 강수 정보 가져오기 - Weather Service
         */
        //TODO : 강수량 확인, 멘트
        sb.append(" 오후 6시부터 8시까지 120mm의 비가 올 예정입니다.");

        /**
         * 4. 꽃가루 정보 가져오기 - Weather Service
         */
        //TODO : 꽃가루 확인
        sb.append(" 꽃가루는 없습니다. ") ;

        /**
         * 알림 전송
         */
        //TODO : FireBase 알림 보내기
        log.info("[ Notification Service ] Final Message ---> {}", sb.toString());

        //알림 보내기
        alarmService.sendSingleAlarm(email, title, sb.toString());
        return sb.toString();
    }

    //현재 위치 업데이트
    @Transactional
    public void updateLocation(String email, LocationDto locationDto) {

        log.info("[ Notification Service ]  email ---> {}", email);
        log.info("[ Notification Service ]  현재 위치 위도 (lat) ---> {}", locationDto.lat());
        log.info("[ Notification Service ]  현재 위치 경도 (lon) ---> {}", locationDto.lon());

        Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(email);

        //TODO : 위도 경도 -> 지역 변환
        String region = "서울특별시";

        if (notiRecord.isPresent()) {
            NotificationRecord notificationRecord = notiRecord.get();

            if (!notiRecord.get().getRegion().equals(region)) {
                //만약 위치가 이동됐다면 알림 시간 초기화
                notificationRecord.setLastWindAlarmReceived(LocalDateTime.now().minusHours(4));
                notificationRecord.setLastRainAlarmReceived(LocalDateTime.now().minusHours(4));
            }
            notificationRecord.setRegion(region);

        } else notificationRecordRepository.save(
                NotificationRecord.builder()
                .email(email)
                .region(region)
                .lastRainAlarmReceived(LocalDateTime.now().minusHours(4))
                .lastWindAlarmReceived(LocalDateTime.now().minusHours(4))
                .build()
        );




    }
}
