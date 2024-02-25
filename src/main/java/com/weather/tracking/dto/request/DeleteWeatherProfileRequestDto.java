package com.weather.tracking.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DeleteWeatherProfileRequestDto {
    @NotNull(message = "Weather Profile ID cannot be null when attempting to delete a weather profile")
    private Long id;
    @NotEmpty(message = "User email cannot be empty when attempting to delete a weather profile")
    @Email(message = "User email must be of email form when attempting to delete a weather profile")
    private String userEmail;
}
