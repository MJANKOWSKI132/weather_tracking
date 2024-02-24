package com.weather.tracking.controller;

import com.weather.tracking.client.OpenWeatherClient;
import com.weather.tracking.dto.response.CityWeatherResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TestController {
    private final OpenWeatherClient openWeatherClient;
    private static final String API_KEY = "8e3436bc12e012bc870174234b344152";

    public TestController(final OpenWeatherClient openWeatherClient) {
        this.openWeatherClient = openWeatherClient;
    }

    @GetMapping("/weather")
    public ResponseEntity<CityWeatherResponseDto> getWeatherForCity(@RequestParam String cityName) {
        return ResponseEntity.ok(openWeatherClient.getCityLatLong(cityName, API_KEY, Optional.empty()));
    }
}
