package com.weather.tracking.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class Auditable {
    @Column(name = "time_created", nullable = false, updatable = false)
    private ZonedDateTime timeCreated;
    @Column(name = "last_modified")
    private ZonedDateTime lastModified;
    @Column(name = "version", nullable = false)
    private int version;

    @PrePersist
    protected void onCreate() {
        this.timeCreated = ZonedDateTime.now();
        this.version = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModified = ZonedDateTime.now();
        this.version++;
    }
}
