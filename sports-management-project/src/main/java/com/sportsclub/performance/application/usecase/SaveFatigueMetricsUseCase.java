package com.sportsclub.performance.application.usecase;

import com.sportsclub.performance.domain.port.out.FatigueMetricsRepository;
import com.sportsclub.shared.domain.model.FatigueLevel;
import java.time.LocalDateTime;
import java.util.UUID;

public class SaveFatigueMetricsUseCase {
    private final FatigueMetricsRepository fatigueMetricsRepository;
    public SaveFatigueMetricsUseCase(FatigueMetricsRepository fatigueMetricsRepository) { this.fatigueMetricsRepository = fatigueMetricsRepository; }

    public void execute(UUID athleteId, FatigueLevel fatigueLevel, LocalDateTime calculatedAt) {
        if (athleteId == null || fatigueLevel == null) throw new IllegalArgumentException("Athlete ID and fatigue level cannot be null");
        fatigueMetricsRepository.save(athleteId, fatigueLevel, calculatedAt);
    }
}