package com.waither.notiservice.repository;

import com.waither.notiservice.domain.UserMedian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMedianRepository extends JpaRepository<UserMedian, Long> {
}
