package com.weather.tracking.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeatherProfileCreationRequestDto extends WeatherProfileModificationRequestDto {
    @NotEmpty(message = "Nickname cannot be empty when modifying weather profile")
    private String nickname;
}
