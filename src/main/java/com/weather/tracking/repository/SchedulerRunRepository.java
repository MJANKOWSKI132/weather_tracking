package com.weather.tracking.repository;

import com.weather.tracking.entity.SchedulerRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// TODO: check if a scheduler run conflicts with a currently running schedule run
@Repository
public interface SchedulerRunRepository extends JpaRepository<SchedulerRun, Long> {
}
