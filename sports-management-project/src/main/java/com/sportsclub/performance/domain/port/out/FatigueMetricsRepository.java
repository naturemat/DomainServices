package com.sportsclub.performance.domain.port.out;

import com.sportsclub.performance.domain.model.entity.FatigueMetrics;
import com.sportsclub.shared.domain.model.FatigueLevel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FatigueMetricsRepository {
    void save(UUID athleteId, FatigueLevel fatigueLevel, LocalDateTime calculatedAt);
    Optional<FatigueLevel> findLatestByAthleteId(UUID athleteId);
    List<FatigueMetrics> findByAthleteId(UUID athleteId);
    List<FatigueMetrics> findByAthleteIdAndDateRange(UUID athleteId, LocalDateTime start, LocalDateTime end);
}