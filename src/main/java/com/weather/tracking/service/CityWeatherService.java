package com.weather.tracking.service;

import com.weather.tracking.client.OpenWeatherClient;
import com.weather.tracking.dto.response.OpenWeatherCityWeatherResponseDto;
import com.weather.tracking.entity.City;
import com.weather.tracking.entity.CityWeather;
import com.weather.tracking.repository.CityRepository;
import com.weather.tracking.repository.CityWeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CityWeatherService {
    private final CityWeatherRepository cityWeatherRepository;
    private final CityRepository cityRepository;
    private final OpenWeatherClient openWeatherClient;

    private static final String API_KEY = "8e3436bc12e012bc870174234b344152";

    public CityWeatherService(final CityWeatherRepository cityWeatherRepository,
                              final CityRepository cityRepository,
                              final OpenWeatherClient openWeatherClient) {
        this.cityWeatherRepository = cityWeatherRepository;
        this.cityRepository = cityRepository;
        this.openWeatherClient = openWeatherClient;
    }

    @Transactional
    public void pollCityWeatherInformation() {
        List<City> cityList = cityRepository.findAll();
        pollCityWeatherInformation(cityList);
    }

    @Transactional
    public void pollCityWeatherInformation(List<City> cityList) {
        List<CompletableFuture<Optional<CityWeather>>> weatherFutures = new ArrayList<>();
        for (City city : cityList) {
            CompletableFuture<Optional<CityWeather>> weatherFuture = CompletableFuture
                    .supplyAsync(() -> {
                        OpenWeatherCityWeatherResponseDto responseDto = openWeatherClient.getCityLatLong(city.getName(), API_KEY, Optional.empty());
                        CityWeather cityWeather = CityWeather.fromDto(responseDto);
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
    }
}
