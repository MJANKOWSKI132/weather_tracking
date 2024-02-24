package com.weather.tracking.repository;

import com.weather.tracking.entity.WeatherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherProfileRepository extends JpaRepository<WeatherProfile, Long> {
}
