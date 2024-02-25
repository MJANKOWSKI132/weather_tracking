package com.weather.tracking.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class City extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Getter
    @Column(unique = true, updatable = false, nullable = false)
    private String name;
    @OneToOne(mappedBy = "city")
    private CityWeather cityWeather;
//    @OneToMany(mappedBy = "city")
//    private Set<CityWeatherProfile> cityWeatherProfiles = new HashSet<>();

//    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
//    @JoinTable(
//            name = "city_weather_profile",
//            joinColumns = @JoinColumn(name = "city_id"),
//            inverseJoinColumns = @JoinColumn(name = "weather_profile_id")
//    )
//    private Set<WeatherProfile> weatherProfiles;

    public City(String name) {
        this.name = name;
    }
}
