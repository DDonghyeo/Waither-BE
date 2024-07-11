package com.waither.userservice.repository;

import com.waither.userservice.entity.Survey;
import com.waither.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {

    Survey findByUserId(Long UserId);

    void deleteAllByUser(User user);
}
