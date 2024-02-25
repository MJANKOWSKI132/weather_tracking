package com.weather.tracking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.tracking.client.OpenWeatherClient;
import com.weather.tracking.dto.response.OpenWeatherCityWeatherResponseDto;
import com.weather.tracking.entity.City;
import com.weather.tracking.entity.CityWeather;
import com.weather.tracking.repository.CityRepository;
import com.weather.tracking.repository.CityWeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CityWeatherService {
    private final CityRepository cityRepository;
    private final OpenWeatherClient openWeatherClient;
    private final CityWeatherRepository cityWeatherRepository;

    private static final String API_KEY = "8e3436bc12e012bc870174234b344152";

    public CityWeatherService(final CityRepository cityRepository,
                              final OpenWeatherClient openWeatherClient,
                              final CityWeatherRepository cityWeatherRepository) {
        this.cityRepository = cityRepository;
        this.openWeatherClient = openWeatherClient;
        this.cityWeatherRepository = cityWeatherRepository;
    }

    @Transactional
    public long pollCityWeatherInformation() {
        List<City> cityList = cityRepository.findAll();
        return pollCityWeatherInformation(cityList);
    }

    @Transactional
    public long pollCityWeatherInformation(List<City> cityList) {
        List<CompletableFuture<Optional<CityWeather>>> weatherFutures = new ArrayList<>();
        for (City city : cityList) {
            CompletableFuture<Optional<CityWeather>> weatherFuture = CompletableFuture
                    .supplyAsync(() -> {
                        OpenWeatherCityWeatherResponseDto responseDto = openWeatherClient.getCityLatLong(city.getName(), API_KEY, Optional.empty());
                        CityWeather cityWeather;
                        if (Objects.isNull(city.getCityWeather())) {
                            cityWeather = CityWeather.fromDto(responseDto);
                        } else {
                            cityWeather = city.getCityWeather();
                            cityWeather.modifyData(responseDto);
                        }
                        cityWeather.setTimeRetrieved(ZonedDateTime.now());
                        cityWeather.setCity(city);
                        return Optional.of(cityWeather);
                    })
                    .exceptionally(ex -> {
                        log.error("An error occurred whilst retrieving weather information for the city with name: {}", city.getName(), ex);
                        return Optional.empty();
                    });
            weatherFutures.add(weatherFuture);
        }
        CompletableFuture
                .allOf(weatherFutures.toArray(new CompletableFuture[0]))
                .join();
        List<CityWeather> cityWeatherList = weatherFutures.stream()
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        cityWeatherRepository.saveAll(cityWeatherList);
        return cityWeatherList.size();
    }
}
