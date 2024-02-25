package com.weather.tracking.service;

import com.weather.tracking.client.OpenWeatherClient;
import com.weather.tracking.client.OpenWeatherFeignClient;
import com.weather.tracking.dto.response.OpenWeatherCityWeatherResponseDto;
import com.weather.tracking.entity.City;
import com.weather.tracking.entity.CityWeather;
import com.weather.tracking.repository.CityRepository;
import com.weather.tracking.repository.CityWeatherRepository;
import com.weather.tracking.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CityWeatherService {
    private final CityRepository cityRepository;
    private final OpenWeatherClient openWeatherClient;
    private final CityWeatherRepository cityWeatherRepository;

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
        List<CompletableFuture<Optional<CityWeather>>> weatherFutures = new ArrayList<>();
        for (City city : cityList) {
            CompletableFuture<Optional<CityWeather>> weatherFuture = CompletableFuture
                    .supplyAsync(() -> {
                        Optional<OpenWeatherCityWeatherResponseDto> optionalOpenWeatherCityWeatherResponse = openWeatherClient.getWeatherInformation(city.getName(), Constants.METRIC);
                        if (optionalOpenWeatherCityWeatherResponse.isEmpty())
                            return Optional.<CityWeather>empty();
                        OpenWeatherCityWeatherResponseDto responseDto = optionalOpenWeatherCityWeatherResponse.get();
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
                .collect(Collectors.toList());
        cityWeatherRepository.saveAll(cityWeatherList);
        return cityWeatherList.size();
    }
}
