package com.weather.tracking.client;

import com.weather.tracking.dto.response.OpenWeatherCityWeatherResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "open-weather-client", url = "https://api.openweathermap.org/data/2.5/weather")
public interface OpenWeatherClient {
    @GetMapping
    OpenWeatherCityWeatherResponseDto getCityLatLong(@RequestParam(name = "q") String cityName,
                                                     @RequestParam(name = "appId") String apiKey,
                                                     @RequestParam(defaultValue = "metric") Optional<String> optionalUnits);
}
