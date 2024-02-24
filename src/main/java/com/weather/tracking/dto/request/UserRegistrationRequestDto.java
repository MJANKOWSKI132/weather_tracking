package com.weather.tracking.dto.request;

import lombok.Data;
import lombok.Value;

@Data
public class UserRegistrationRequestDto {
    private String name;
    private String email;
}
