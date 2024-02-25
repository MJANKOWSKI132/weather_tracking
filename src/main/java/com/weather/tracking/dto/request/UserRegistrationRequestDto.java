package com.weather.tracking.dto.request;

import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserRegistrationRequestDto {
    @NotEmpty(message = "Name cannot be empty when registering a new user")
    private String name;
    @NotEmpty(message = "User email cannot be empty when registering a new user")
    @Email(message = "User email must be of email form")
    private String userEmail;
}
