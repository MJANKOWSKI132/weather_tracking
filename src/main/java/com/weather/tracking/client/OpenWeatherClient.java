package com.weather.tracking.client;

import com.weather.tracking.dto.response.OpenWeatherCityWeatherResponseDto;
import com.weather.tracking.exception.OpenWeatherException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class OpenWeatherClient {
    private final OpenWeatherFeignClient openWeatherFeignClient;

    @Value("${apiKey}")
    private String apiKey;

    public OpenWeatherClient(OpenWeatherFeignClient openWeatherFeignClient) {
        this.openWeatherFeignClient = openWeatherFeignClient;
    }

    @Retryable(value = OpenWeatherException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @SneakyThrows
    public Optional<OpenWeatherCityWeatherResponseDto> getWeatherInformation(String cityName, String units) {
        ResponseEntity<OpenWeatherCityWeatherResponseDto> responseEntity = openWeatherFeignClient.getWeatherInformation(cityName, apiKey, units);
        if (responseEntity.getStatusCode().is5xxServerError() || Objects.isNull(responseEntity.getBody()))
            throw new OpenWeatherException("Open Weather API returned a non 2XX status code");
        return Optional.of(responseEntity.getBody());
    }
}
