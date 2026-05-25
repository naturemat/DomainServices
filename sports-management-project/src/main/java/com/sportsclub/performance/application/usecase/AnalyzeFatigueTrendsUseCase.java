package com.sportsclub.performance.application.usecase;

import com.sportsclub.performance.domain.model.entity.FatigueMetrics;
import com.sportsclub.performance.domain.port.out.FatigueMetricsRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AnalyzeFatigueTrendsUseCase {
    private final FatigueMetricsRepository fatigueMetricsRepository;

    public AnalyzeFatigueTrendsUseCase(FatigueMetricsRepository fatigueMetricsRepository) {
        this.fatigueMetricsRepository = fatigueMetricsRepository;
    }

    public FatigueTrendResult analyzeTrends(UUID athleteId, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<FatigueMetrics> metrics = fatigueMetricsRepository.findByAthleteIdAndDateRange(athleteId, startDate,
                LocalDateTime.now());
        if (metrics.isEmpty())
            return new FatigueTrendResult(FatigueTrendType.NO_DATA, 0, 0);
        int avgFatigue = metrics.stream().mapToInt(m -> m.getFatigueLevel().getValue()).sum() / metrics.size();
        FatigueTrendType trendType = determineTrend(metrics);
        return new FatigueTrendResult(trendType, avgFatigue, metrics.size());
    }

    private FatigueTrendType determineTrend(List<FatigueMetrics> metrics) {
        if (metrics.size() < 2)
            return FatigueTrendType.STABLE;
        int recentAvg = metrics.stream().skip(Math.max(0, metrics.size() - 3))
                .mapToInt(m -> m.getFatigueLevel().getValue()).sum() / Math.min(3, metrics.size());
        int olderAvg = metrics.stream().limit(Math.max(1, metrics.size() / 2))
                .mapToInt(m -> m.getFatigueLevel().getValue()).sum() / Math.max(1, metrics.size() / 2);
        if (recentAvg > olderAvg + 0.5)
            return FatigueTrendType.DECLINING;
        if (recentAvg < olderAvg - 0.5)
            return FatigueTrendType.IMPROVING;
        return FatigueTrendType.STABLE;
    }

    public record FatigueTrendResult(FatigueTrendType trendType, int averageFatigue, int dataPoints) {
    }

    public enum FatigueTrendType {
        IMPROVING, DECLINING, STABLE, NO_DATA
    }
}