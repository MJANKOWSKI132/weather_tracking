package com.weather.tracking.repository;

import com.weather.tracking.entity.SchedulerRun;
import com.weather.tracking.enums.SchedulerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


// TODO: check if a scheduler run conflicts with a currently running schedule run
@Repository
public interface SchedulerRunRepository extends JpaRepository<SchedulerRun, Long> {
    Optional<SchedulerRun> findTopByTimeFinishedIsNotNullAndStatusAndJobIdsOrderByTimeStartedDesc(SchedulerStatus status, String jobId);
    Optional<SchedulerRun> findTopByTimeFinishedIsNullAndStatusAndJobIdOrderByTimeStartedDesc(SchedulerStatus status, String jobId);
}
