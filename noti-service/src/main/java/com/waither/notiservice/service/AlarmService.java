package com.waither.notiservice.service;

import com.waither.notiservice.api.request.TokenDto;
import com.waither.notiservice.domain.Notification;
import com.waither.notiservice.global.exception.CustomException;
import com.waither.notiservice.global.response.ErrorCode;
import com.waither.notiservice.repository.jpa.NotificationRepository;
import com.waither.notiservice.utils.FireBaseUtils;
import com.waither.notiservice.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {

    private final RedisUtils redisUtils;
    private final FireBaseUtils fireBaseUtils;
    private final NotificationRepository notificationRepository;

    public void updateToken(String email, TokenDto tokenDto) {
        redisUtils.save(email, tokenDto.token());
    }

    public void sendSingleAlarm(String email, String title, String message) {
        String token = String.valueOf(redisUtils.get(email));
        fireBaseUtils.sendSingleMessage(token, title, message);
        notificationRepository.save(Notification.builder()
                .email(email)
                .title(title)
                .content(message)
                .build());
    }

    public void sendAlarms(List<String> userEmails, String title, String message) {
        List<String> tokens = userEmails.stream()
                .map(email -> String.valueOf(redisUtils.get(email)))
                .toList();

        log.info("[ 푸시알림 ] Email ---> {}", userEmails);
        log.info("[ 푸시알림 ] message ---> {}", message);

        fireBaseUtils.sendAllMessages(tokens,title, message);

        List<Notification> notifications = userEmails.stream()
                .map(email -> Notification.builder()
                        .email(email)
                        .title(title)
                        .content(message)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }
}
