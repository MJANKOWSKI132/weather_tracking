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
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, updatable = false, nullable = false)
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @OneToMany(mappedBy = "parentUser", cascade = CascadeType.ALL)
    private List<WeatherProfile> weatherProfiles;
}
