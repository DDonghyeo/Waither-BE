package com.waither.weatherservice.repository;

import org.springframework.data.repository.CrudRepository;

import com.waither.weatherservice.entity.WeatherAdvisory;

public interface WeatherAdvisoryRepository extends CrudRepository<WeatherAdvisory, String> {
}
