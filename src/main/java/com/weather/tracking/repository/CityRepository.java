package com.weather.tracking.repository;

import com.weather.tracking.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    boolean existsByName(String name);
    Set<City> findAllByNameIn(Set<String> name);
}
