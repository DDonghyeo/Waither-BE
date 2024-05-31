package com.waither.notiservice.repository.jpa;

import com.waither.notiservice.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findAllByEmail(String email);
}
