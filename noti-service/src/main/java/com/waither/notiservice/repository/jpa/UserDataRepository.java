package com.waither.notiservice.repository.jpa;

import com.waither.notiservice.domain.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, String> {

    Optional<UserData> findByEmail(String email);
}
