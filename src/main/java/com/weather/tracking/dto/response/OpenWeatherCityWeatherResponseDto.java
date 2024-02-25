package com.weather.tracking.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class OpenWeatherCityWeatherResponseDto extends BaseCityWeatherResponseDto {
    @JsonProperty("weather")
    private List<BaseCityWeatherResponseDto.Weather> weatherList;
}


