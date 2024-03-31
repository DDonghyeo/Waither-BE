package com.waither.weatherservice.redis;

import org.springframework.data.repository.CrudRepository;

import com.waither.weatherservice.entity.DisasterMessage;

public interface DisasterMessageRepository extends CrudRepository<DisasterMessage, String> {
}
