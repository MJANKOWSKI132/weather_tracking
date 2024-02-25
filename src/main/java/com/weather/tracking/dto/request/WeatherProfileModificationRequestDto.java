package com.weather.tracking.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public abstract class WeatherProfileModificationRequestDto {
    @NotEmpty(message = "City names cannot be empty when modifying weather profile")
    @NotNull(message = "City names cannot be null when modifying weather profile")
    private Set<String> cityNames;
    @NotEmpty(message = "User email cannot be empty when modifying weather profile")
    @Email(message = "User email must be of email form")
    private String userEmail;
}
