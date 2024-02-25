package com.weather.tracking.listener;

import com.weather.tracking.entity.City;
import com.weather.tracking.repository.CityRepository;
import com.weather.tracking.service.CityWeatherService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        List<String> initialCityNames = List.of("sydney", "melbourne", "adelaide", "perth", "darwin", "canberra");
        List<City> initialCities = new ArrayList<>();
        for (String initalCityName : initialCityNames) {
            boolean cityAlreadyExists = cityRepository.existsByName(initalCityName);
            if (cityAlreadyExists)
                continue;
            initialCities.add(new City(initalCityName));
        }
        cityRepository.saveAll(initialCities);
        cityWeatherService.pollCityWeatherInformation(initialCities);
    }
}