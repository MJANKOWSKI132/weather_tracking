package com.weather.tracking.repository;

import com.weather.tracking.entity.User;
import com.weather.tracking.entity.WeatherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherProfileRepository extends JpaRepository<WeatherProfile, Long> {
    boolean existsByNicknameAndParentUser(String nickname, User parentUser);
    Optional<WeatherProfile> findByNicknameAndParentUser(String nickname, User parentUser);
}
