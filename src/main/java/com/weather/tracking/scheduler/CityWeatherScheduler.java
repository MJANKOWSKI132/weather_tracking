package com.weather.tracking.scheduler;

import com.weather.tracking.service.CityWeatherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CityWeatherScheduler {
    private final CityWeatherService cityWeatherService;
    private static final long DELAY = 15 * 60 * 1000;

    public CityWeatherScheduler(final CityWeatherService cityWeatherService) {
        this.cityWeatherService = cityWeatherService;
    }

    @Scheduled(initialDelay = DELAY, fixedDelay = DELAY)
    public void pollCityWeatherInformation() {
        cityWeatherService.pollCityWeatherInformation();
    }
}
