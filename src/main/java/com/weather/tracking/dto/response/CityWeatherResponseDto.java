package com.weather.tracking.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CityWeatherResponseDto(Main main, Wind wind, @JsonProperty("weather") List<Weather> weatherList) {

}

@JsonIgnoreProperties(ignoreUnknown = true)
record Wind(double speed) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record Weather(String main) {}

record Main(double temp,
            @JsonProperty("feels_like") double feelsLike,
            @JsonProperty("temp_min") double tempMin,
            @JsonProperty("temp_max") double tempMax,
            double pressure,
            double humidity) { }

