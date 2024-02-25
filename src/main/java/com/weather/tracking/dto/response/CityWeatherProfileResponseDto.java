package com.weather.tracking.dto.response;

import com.weather.tracking.entity.City;
import com.weather.tracking.entity.CityWeather;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class CityWeatherProfileResponseDto extends BaseCityWeatherResponseDto {
    private String cityName;
    private Weather weather;

    public static CityWeatherProfileResponseDto fromEntity(City city) {
        CityWeatherProfileResponseDto response = new CityWeatherProfileResponseDto();

        CityWeather cityWeather = city.getCityWeather();

        Weather weather = new Weather();
        BeanUtils.copyProperties(cityWeather.getWeather(), weather);
        response.setWeather(weather);

        response.setCityName(cityWeather.getCity().getName());

        Main main = new Main();
        BeanUtils.copyProperties(cityWeather.getMain(), main);
        response.setMain(main);

        Wind wind = new Wind();
        BeanUtils.copyProperties(cityWeather.getWind(), wind);
        response.setWind(wind);

        return response;
    }
}
