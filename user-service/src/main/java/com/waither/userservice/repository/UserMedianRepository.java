package com.waither.userservice.repository;

import com.waither.userservice.entity.User;
import com.waither.userservice.entity.UserData;
import com.waither.userservice.entity.UserMedian;
import com.waither.userservice.entity.enums.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMedianRepository extends JpaRepository<UserMedian, Integer> {
    Optional<UserMedian> findByUserAndSeason(User user, Season season);

    void deleteAllByUser(User user);

}
