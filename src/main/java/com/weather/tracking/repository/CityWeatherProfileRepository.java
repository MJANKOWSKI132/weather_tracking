package com.weather.tracking.repository;

import com.weather.tracking.entity.CityWeatherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityWeatherProfileRepository extends JpaRepository<CityWeatherProfile, Long> {
}
