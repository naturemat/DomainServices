package com.sportsclub.infrastructure.adapter.out.persistence.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "fatigue_metrics")
public class FatigueMetricsDocument {
    @Id private String id;
    private UUID athleteId;
    private String fatigueLevel;
    private LocalDateTime calculatedAt;

    public FatigueMetricsDocument() {}
    public FatigueMetricsDocument(UUID athleteId, String fatigueLevel, LocalDateTime calculatedAt) { this.athleteId = athleteId; this.fatigueLevel = fatigueLevel; this.calculatedAt = calculatedAt; }
    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public UUID getAthleteId() { return athleteId; } public void setAthleteId(UUID athleteId) { this.athleteId = athleteId; }
    public String getFatigueLevel() { return fatigueLevel; } public void setFatigueLevel(String fatigueLevel) { this.fatigueLevel = fatigueLevel; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; } public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}