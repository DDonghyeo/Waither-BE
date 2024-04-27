package com.waither.notiservice.service;

import com.waither.notiservice.api.response.NotificationResponse;
import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.dto.LocationDto;
import com.waither.notiservice.repository.NotificationRepository;
import com.waither.notiservice.repository.UserDataRepository;
import com.waither.notiservice.repository.UserMedianRepository;
import com.waither.notiservice.utils.TemperatureUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserMedianRepository userMedianRepository;

    private final UserDataRepository userDataRepository;


    public List<NotificationResponse> getNotifications(Long userId) {

        return notificationRepository.findAllByUserId(userId)
                .stream().map(NotificationResponse::of).toList();
    }

    public String sendGoOutAlarm(Long userId) {

        UserData userData = userDataRepository.findById(userId).orElseThrow();

        String finalMessage = "";

        /**
         * 당일 온도 정리 - Weather Service
         * {@link com.waither.notiservice.enums.Expressions} 참고
         */
        //TODO : 날씨 정보 가져오기 & 날씨별 멘트 정리
        String nickName = userData.getNickName();
        finalMessage += nickName + "님, 오늘은 ";

        double temperature = 10.8;
        if (userData.isUserAlert()) {
            //사용자 맞춤 알림이 on이라면 -> 계산 후 전용 정보 제공
            UserMedian userMedian = userMedianRepository.findById(userId).orElseThrow();
            finalMessage += TemperatureUtils.createUserDataMessage(userMedian, temperature);
        } else {
            //사용자 맞춤 알림이 off라면 -> 하루 평균 온도 정보 제공
            finalMessage += "평균 온도가 "+temperature+"도예요.";
        }

        /**
         * 미세먼지 정보 가져오기 - Weather Service
         */
        //TODO : 미세먼지 On/Off 확인, 멘트
        finalMessage += " 미세먼지는 없습니다.";

        /**
         * 강수 정보 가져오기 - Weather Service
         */
        //TODO : 강수량 확인, 멘트
        finalMessage += " 오후 6시부터 8시까지 120mm의 비가 올 예정입니다.";

        /**
         * 꽃가로 정보 가져오기 - Weather Service
         */
        //TODO : 꽃가루 확인
        finalMessage += " 꽃가루는 없습니다. ";

        /**
         * 알림 전송
         */
        //TODO : FireBase 알림 보내기
        log.info("[ Notification Service ] Final Message ---> {}", finalMessage);

        return finalMessage;
    }

    //현재 위치 공유 -> 상시 알림 검사
    public void checkCurrentAlarm(LocationDto locationDto) {

        //TODO : 현재 지역에 강수량 정보가 있는지?

        //TODO : 현재 지역에 바람 세기 정보는 있는지?

        //TODO : 만약 알림 내용이 있다면 전송하기
    }
}
