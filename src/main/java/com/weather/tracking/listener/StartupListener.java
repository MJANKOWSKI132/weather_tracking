package com.weather.tracking.listener;

import com.weather.tracking.client.OpenWeatherClient;
import com.weather.tracking.dto.response.CityWeatherResponseDto;
import com.weather.tracking.entity.City;
import com.weather.tracking.entity.CityWeather;
import com.weather.tracking.repository.CityRepository;
import com.weather.tracking.repository.CityWeatherRepository;
import com.weather.tracking.service.CityWeatherService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class StartupListener implements ApplicationRunner {
    private final CityRepository cityRepository;
    private final CityWeatherService cityWeatherService;

    public StartupListener(final CityRepository cityRepository,
                           final CityWeatherService cityWeatherService) {
        this.cityRepository = cityRepository;
        this.cityWeatherService = cityWeatherService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<City> initialCities = List.of(
                new City("sydney"),
                new City("melbourne"),
                new City("adelaide"),
                new City("perth"),
                new City("darwin"),
                new City("canberra")
        );
        cityRepository.saveAll(initialCities);
        cityWeatherService.pollCityWeatherInformation(initialCities);
    }
}