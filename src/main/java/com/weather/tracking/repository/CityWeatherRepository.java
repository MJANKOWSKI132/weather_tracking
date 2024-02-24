package com.weather.tracking.repository;

import com.weather.tracking.entity.CityWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityWeatherRepository extends JpaRepository<CityWeather, Long> {
}
