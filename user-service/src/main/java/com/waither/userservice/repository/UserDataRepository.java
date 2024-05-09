package com.waither.userservice.repository;

import com.waither.userservice.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepository extends JpaRepository<UserData, Integer> {
}
