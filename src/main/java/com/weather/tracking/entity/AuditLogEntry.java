package com.weather.tracking.entity;

import com.weather.tracking.enums.ActionStatus;
import com.weather.tracking.enums.AuditAction;
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
public class AuditLogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, updatable = false)
    private ZonedDateTime timeStarted;
    private ZonedDateTime timeFinished;
    @Enumerated(EnumType.STRING)
    private ActionStatus status;
    @Enumerated(EnumType.STRING)
    private AuditAction action;
    private String userEmail;
    private String additionalContext;

    public AuditLogEntry(AuditAction auditAction, String userEmail) {
        this.timeStarted = ZonedDateTime.now();
        this.action = auditAction;
        this.userEmail = userEmail;
    }
}
