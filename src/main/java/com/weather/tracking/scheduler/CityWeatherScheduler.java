package com.weather.tracking.scheduler;

import com.weather.tracking.service.CityWeatherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CityWeatherScheduler {
    private final CityWeatherService cityWeatherService;

    public CityWeatherScheduler(final CityWeatherService cityWeatherService) {
        this.cityWeatherService = cityWeatherService;
    }

    @Scheduled(initialDelay = 15 * 60 * 1000, fixedDelay = 15 * 60 * 1000)
    public void pollCityWeatherInformation() {
        cityWeatherService.pollCityWeatherInformation();
    }
}
