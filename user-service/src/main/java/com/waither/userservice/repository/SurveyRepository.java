package com.waither.userservice.repository;

import com.waither.userservice.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {
}
