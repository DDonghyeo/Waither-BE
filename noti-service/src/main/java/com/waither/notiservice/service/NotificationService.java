package com.waither.notiservice.service;

import com.waither.notiservice.api.response.NotificationResponse;
import com.waither.notiservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public List<NotificationResponse> getNotifications(Long userId) {

        return notificationRepository.findAllByUserId(userId)
                .stream().map(NotificationResponse::of).toList();
    }
}
