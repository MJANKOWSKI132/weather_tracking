package com.weather.tracking.client;

import com.weather.tracking.dto.response.OpenWeatherCityWeatherResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Can add retry mechanism to this Feign client
@FeignClient(name = "open-weather-client", url = "${open-weather-url}")
public interface OpenWeatherFeignClient {
    @GetMapping("/data/2.5/weather")
    ResponseEntity<OpenWeatherCityWeatherResponseDto> getWeatherInformation(@RequestParam(name = "q") String cityName,
                                                                            @RequestParam(name = "appId") String apiKey,
                                                                            @RequestParam(defaultValue = "metric", name = "units") String units);
}
