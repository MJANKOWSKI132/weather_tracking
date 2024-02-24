package com.weather.tracking.dto.request;

import lombok.Value;

@Value
public class UserRegistrationRequestDto {
    private String name;
    private String email;
}
