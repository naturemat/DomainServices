package application.dto;

import domain.model.valueobjects.FatigueLevel;
import java.time.LocalDateTime;
import java.util.UUID;

public class FatigueMetricsDTO {
    private UUID athleteId;
    private FatigueLevel fatigueLevel;
    private LocalDateTime calculatedAt;

    public FatigueMetricsDTO() {}

    public FatigueMetricsDTO(UUID athleteId, FatigueLevel fatigueLevel, LocalDateTime calculatedAt) {
        this.athleteId = athleteId;
        this.fatigueLevel = fatigueLevel;
        this.calculatedAt = calculatedAt;
    }

    public UUID getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(UUID athleteId) {
        this.athleteId = athleteId;
    }

    public FatigueLevel getFatigueLevel() {
        return fatigueLevel;
    }

    public void setFatigueLevel(FatigueLevel fatigueLevel) {
        this.fatigueLevel = fatigueLevel;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}