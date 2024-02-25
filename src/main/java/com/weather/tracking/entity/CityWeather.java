package com.weather.tracking.entity;

import com.weather.tracking.dto.response.OpenWeatherCityWeatherResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
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
@NoArgsConstructor
public class CityWeather extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "city_id")
    private City city;
    @Embedded
    private Main main;
    @Embedded
    private Weather weather;
    @Embedded
    private Wind wind;
    @Column(nullable = false)
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
            throw new RuntimeException("Error, the weather list returned from OpenWeather is empty");
        BeanUtils.copyProperties(dto.getWeatherList().get(0), weather);
        cityWeather.setWeather(weather);

        return cityWeather;
    }

    public void modifyData(OpenWeatherCityWeatherResponseDto dto) {
        BeanUtils.copyProperties(dto.getWind(), this.wind);
        BeanUtils.copyProperties(dto.getMain(), this.main);

        if (CollectionUtils.isEmpty(dto.getWeatherList()))
            throw new RuntimeException("Error, the weather list returned from OpenWeather is empty");
        BeanUtils.copyProperties(dto.getWeatherList().get(0), this.weather);
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class Wind {
        @Column(nullable = false)
        private double speed;
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Weather {
        @Column(nullable = false)
        private String main;
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class Main {
        @Column(nullable = false)
        private double temp;
        @Column(nullable = false)
        private double feelsLike;
        @Column(nullable = false)
        private double tempMin;
        @Column(nullable = false)
        private double tempMax;
        @Column(nullable = false)
        private double pressure;
        @Column(nullable = false)
        private double humidity;
    }
}
