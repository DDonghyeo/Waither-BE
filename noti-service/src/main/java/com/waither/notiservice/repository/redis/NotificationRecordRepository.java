package com.waither.notiservice.repository.redis;

import com.waither.notiservice.domain.redis.NotificationRecord;
import org.springframework.data.repository.CrudRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface NotificationRecordRepository extends CrudRepository<NotificationRecord, String > {

    Optional<NotificationRecord> findByEmail(String email);
}
