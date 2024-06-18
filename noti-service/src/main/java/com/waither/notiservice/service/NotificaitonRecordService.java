package com.waither.notiservice.service;

import com.waither.notiservice.domain.redis.NotificationRecord;
import com.waither.notiservice.global.exception.CustomException;
import com.waither.notiservice.repository.redis.NotificationRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NotificaitonRecordService {

    private final NotificationRecordRepository notificationRecordRepository;

    public void updateWindAlarm(String email) {
        Optional<NotificationRecord> notificationRecord = notificationRecordRepository.findByEmail(email);

        notificationRecord.ifPresentOrElse(record -> record.setLastWindAlarmReceived(LocalDateTime.now()), null);
    }

    public void updateRainAlarm(String email) {
        Optional<NotificationRecord> notificationRecord = notificationRecordRepository.findByEmail(email);
        notificationRecord.ifPresent(record -> record.setLastRainAlarmReceived(LocalDateTime.now()));
    }


}
