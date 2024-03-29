package com.waither.weatherservice.redis;

import org.springframework.data.repository.CrudRepository;

import com.waither.weatherservice.entity.DailyWeather;

public interface DailyWeatherRepository extends CrudRepository<DailyWeather, String> {
}
