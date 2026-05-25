package com.sportsclub.performance.application.usecase;

import com.sportsclub.performance.domain.model.entity.FatigueMetrics;
import com.sportsclub.performance.domain.port.out.FatigueMetricsRepository;
import com.sportsclub.shared.domain.model.FatigueLevel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetFatigueHistoryUseCase {
    private final FatigueMetricsRepository fatigueMetricsRepository;

    public GetFatigueHistoryUseCase(FatigueMetricsRepository fatigueMetricsRepository) {
        this.fatigueMetricsRepository = fatigueMetricsRepository;
    }

    public Optional<FatigueLevel> getLatestFatigueLevel(UUID athleteId) {
        return fatigueMetricsRepository.findLatestByAthleteId(athleteId);
    }

    public List<FatigueMetrics> getFatigueHistory(UUID athleteId, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return fatigueMetricsRepository.findByAthleteIdAndDateRange(athleteId, startDate, LocalDateTime.now());
    }

    public List<FatigueMetrics> getAllFatigueMetrics(UUID athleteId) {
        return fatigueMetricsRepository.findByAthleteId(athleteId);
    }
}