package com.weather.tracking.scheduler;

import com.weather.tracking.entity.SchedulerRun;
import com.weather.tracking.enums.SchedulerStatus;
import com.weather.tracking.repository.SchedulerRunRepository;
import com.weather.tracking.service.CityWeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class CityWeatherScheduler {
    private final CityWeatherService cityWeatherService;
    private final SchedulerRunRepository schedulerRunRepository;
    private static final long DELAY = 15 * 60 * 1000;
    private static final String JOB_ID = CityWeatherScheduler.class.getSimpleName();

    public CityWeatherScheduler(final CityWeatherService cityWeatherService,
                                final SchedulerRunRepository schedulerRunRepository) {
        this.cityWeatherService = cityWeatherService;
        this.schedulerRunRepository = schedulerRunRepository;
    }

    @Scheduled(fixedDelay = DELAY)
    public void pollCityWeatherInformation() {
        Optional<SchedulerRun> optionalCurrentlyRunningScheduleRun = schedulerRunRepository
                .findTopByTimeFinishedIsNullAndStatusAndJobIdOrderByTimeStartedDesc(SchedulerStatus.RUNNING, JOB_ID);
        if (optionalCurrentlyRunningScheduleRun.isPresent()) {
            SchedulerRun schedulerRun = optionalCurrentlyRunningScheduleRun.get();
            long timeElapsedSinceLastRunMS = System.currentTimeMillis() - schedulerRun.getTimeStarted().toInstant().toEpochMilli();
            if (timeElapsedSinceLastRunMS >= DELAY) {
                schedulerRun.completeWithError("Unknown error");
                schedulerRunRepository.save(schedulerRun);
            } else {
                return;
            }
        }
        Optional<SchedulerRun> optionalPreviouslyRunSchedulerRun = schedulerRunRepository
                .findTopByTimeFinishedIsNotNullAndStatusAndJobIdOrderByTimeStartedDesc(SchedulerStatus.RUNNING, JOB_ID);
        if (optionalPreviouslyRunSchedulerRun.isPresent()) {
            SchedulerRun previousRun = optionalPreviouslyRunSchedulerRun.get();
            long timeElapsedSinceLastRunMS = System.currentTimeMillis() - previousRun.getTimeFinished().toInstant().toEpochMilli();
            if (timeElapsedSinceLastRunMS < DELAY)
                return;
        }
        SchedulerRun schedulerRun = new SchedulerRun(JOB_ID);
        schedulerRunRepository.save(schedulerRun);
        try {
            long numberOfCityWeathersModified = cityWeatherService.pollCityWeatherInformation();
            String additionalContext = String.format("A total of %s City Weathers were modified", numberOfCityWeathersModified);
            schedulerRun.completeSuccessfully(additionalContext);
        } catch (Exception ex) {
            String errorMessage = String.format("An error occurred whilst running the Scheduler with Job ID: %s. Cause: %s", JOB_ID, ex.getMessage());
            log.error(errorMessage);
            schedulerRun.completeWithError(errorMessage);
        } finally {
            schedulerRunRepository.save(schedulerRun);
        }
    }
}
