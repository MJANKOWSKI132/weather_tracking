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
import java.util.Set;
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
        Set<String> initialCityNames = Set.of("sydney", "melbourne", "adelaide", "perth", "darwin", "canberra");
        Set<City> allCities = cityRepository.findAllByNameIn(initialCityNames);
        Set<String> citiesThatAlreadyExist = allCities.stream().map(City::getName).collect(Collectors.toSet());
        List<City> initialCities = new ArrayList<>();
        for (String initialCityName : initialCityNames) {
            boolean cityAlreadyExists = citiesThatAlreadyExist.contains(initialCityName);
            if (cityAlreadyExists)
                continue;
            City city = new City(initialCityName);
            initialCities.add(city);
        }
        cityRepository.saveAll(initialCities);
        allCities.addAll(initialCities);
        cityWeatherService.pollCityWeatherInformation(allCities.stream().toList());
    }
}