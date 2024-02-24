package com.weather.tracking.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, updatable = false, nullable = false)
    private String email;

    @OneToMany(mappedBy = "parentUser", cascade = CascadeType.ALL)
    private List<WeatherProfile> weatherProfiles;
}
