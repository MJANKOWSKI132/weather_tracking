package com.weather.tracking.entity;

import lombok.Data;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.Set;

@Entity
public class City extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Getter
    @Column(unique = true, updatable = false, nullable = false)
    private final String name;
    @OneToOne(mappedBy = "city", cascade = CascadeType.ALL)
    private CityWeather cityWeather;
    @ManyToMany
    @JoinTable(
            name = "city_weather_profile",
            joinColumns = @JoinColumn(name = "city_id"),
            inverseJoinColumns = @JoinColumn(name = "weather_profile_id")
    )
    private Set<WeatherProfile> weatherProfiles;

    public City(String name) {
        this.name = name;
    }
}
