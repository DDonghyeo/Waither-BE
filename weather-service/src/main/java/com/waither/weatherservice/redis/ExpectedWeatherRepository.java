package com.waither.weatherservice.redis;

import org.springframework.data.repository.CrudRepository;

import com.waither.weatherservice.entity.ExpectedWeather;

public interface ExpectedWeatherRepository extends CrudRepository<ExpectedWeather, String> {
}
