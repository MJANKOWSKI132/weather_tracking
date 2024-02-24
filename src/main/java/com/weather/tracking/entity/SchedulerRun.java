package com.weather.tracking.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
public class SchedulerRun {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, updatable = false)
    private ZonedDateTime timeStarted;
    private ZonedDateTime timeFinished;
    private String status; // TOOD: change to enum
    private String errorMessage;
    private String additionalContext;
}
