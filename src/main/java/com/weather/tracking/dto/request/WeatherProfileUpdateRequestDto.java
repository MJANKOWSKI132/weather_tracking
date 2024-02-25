package com.weather.tracking.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeatherProfileUpdateRequestDto extends WeatherProfileModificationRequestDto {
    @NotNull(message = "Weather Profile ID cannot be null when updating weather profile")
    private Long id;
    private String nickname;
}
