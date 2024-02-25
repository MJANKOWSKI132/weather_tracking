package com.weather.tracking.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SchedulerRun {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false, updatable = false)
    private ZonedDateTime timeStarted;
    private ZonedDateTime timeFinished;
    private String status; // TOOD: change to enum
    private String errorMessage;
    private String additionalContext;
}
