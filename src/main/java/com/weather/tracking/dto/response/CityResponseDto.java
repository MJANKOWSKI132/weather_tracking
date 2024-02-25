package com.weather.tracking.dto.response;

import com.weather.tracking.entity.City;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CityResponseDto {
    private String name;

    public static CityResponseDto fromEntity(City city) {
        CityResponseDto responseDto = new CityResponseDto();
        responseDto.setName(city.getName());
        return responseDto;
    }
}
