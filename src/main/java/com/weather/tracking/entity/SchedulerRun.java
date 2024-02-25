package com.weather.tracking.entity;

import com.weather.tracking.enums.SchedulerStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class SchedulerRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, updatable = false)
    private ZonedDateTime timeStarted;
    private ZonedDateTime timeFinished;
    @Enumerated(EnumType.STRING)
    private SchedulerStatus status;
    private String additionalContext;
    private String jobId;

    public SchedulerRun(String jobId) {
        this.timeStarted = ZonedDateTime.now();
        this.status = SchedulerStatus.RUNNING;
        this.jobId = jobId;
    }

    public void completeSuccessfully(String additionalContext) {
        this.timeFinished = ZonedDateTime.now();
        this.status = SchedulerStatus.SUCCESS;
        this.additionalContext = additionalContext;
    }

    public void completeWithError(String errorMessage) {
        this.timeFinished = ZonedDateTime.now();
        this.status = SchedulerStatus.FAILED;
        this.additionalContext = errorMessage;
    }
}
