package com.waither.userservice.repository;

import com.waither.userservice.entity.Setting;
import com.waither.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Integer> {

    Optional<Setting> findByUser(User user);
}
