package com.weather.tracking.scheduler;

import com.weather.tracking.service.CityWeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CityWeatherScheduler {
    private final CityWeatherService cityWeatherService;
    private static final long DELAY = 1 * 15 * 1000;

    public CityWeatherScheduler(final CityWeatherService cityWeatherService) {
        this.cityWeatherService = cityWeatherService;
    }

    @Scheduled(initialDelay = DELAY, fixedDelay = DELAY)
    public void pollCityWeatherInformation() {
        log.info("Now making scheduled call to receive new city weather information...");
        cityWeatherService.pollCityWeatherInformation();
    }
}
