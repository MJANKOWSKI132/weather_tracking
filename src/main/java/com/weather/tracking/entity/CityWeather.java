package com.weather.tracking.entity;

import com.weather.tracking.dto.response.OpenWeatherCityWeatherResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class CityWeather extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;
    @OneToOne
    @JoinColumn(name = "city_id")
    private City city;
    @Embedded
    @ToString.Include
    private Main main;
    @Embedded
    @ToString.Include
    private Weather weather;
    @Embedded
    @ToString.Include
    private Wind wind;
    private ZonedDateTime timeRetrieved;

    public static CityWeather fromDto(OpenWeatherCityWeatherResponseDto dto) {
        CityWeather cityWeather = new CityWeather();

        Wind wind = new Wind();
        BeanUtils.copyProperties(dto.getWind(), wind);
        cityWeather.setWind(wind);

        Main main = new Main();
        BeanUtils.copyProperties(dto.getMain(), main);
        cityWeather.setMain(main);

        Weather weather = new Weather();
        if (CollectionUtils.isEmpty(dto.getWeatherList()))
            throw new RuntimeException("ERROR!"); // TODO: modify
        BeanUtils.copyProperties(dto.getWeatherList().get(0), weather);
        cityWeather.setWeather(weather);

        return cityWeather;
    }

    public void modifyData(OpenWeatherCityWeatherResponseDto dto) {
        BeanUtils.copyProperties(dto.getWind(), this.wind);
        BeanUtils.copyProperties(dto.getMain(), this.main);

        Weather weather = new Weather();
        if (CollectionUtils.isEmpty(dto.getWeatherList()))
            throw new RuntimeException("ERROR!"); // TODO: modify
        BeanUtils.copyProperties(dto.getWeatherList().get(0), this.weather);
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class Wind {
        private double speed;
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Weather {
        private String main;
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class Main {
        private double temp;
        private double feelsLike;
        private double tempMin;
        private double tempMax;
        private double pressure;
        private double humidity;
    }
}
