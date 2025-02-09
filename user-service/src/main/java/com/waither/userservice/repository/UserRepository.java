package com.waither.userservice.repository;

import com.waither.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String Email);

    User findById(Long Id);

    boolean existsByEmail(String Email);

}