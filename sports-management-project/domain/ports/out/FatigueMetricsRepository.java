package domain.ports.out;

import domain.model.valueobjects.FatigueLevel;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface FatigueMetricsRepository {
    void save(UUID athleteId, FatigueLevel fatigueLevel, LocalDateTime calculatedAt);
    Optional<FatigueLevel> findLatestByAthleteId(UUID athleteId);
}