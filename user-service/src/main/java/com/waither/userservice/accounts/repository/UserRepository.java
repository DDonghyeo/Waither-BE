package com.waither.userservice.accounts.repository;

import com.waither.userservice.accounts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String Email);

}