package com.sportsclub.infrastructure.adapter.out.persistence.postgres.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "routines")
public class RoutineEntity {
    @Id private UUID id;
    @Column(name = "athlete_id", nullable = false) private UUID athleteId;
    @Column(name = "name", nullable = false) private String name;
    @Column(name = "description") private String description;
    @Column(name = "recommended_duration_minutes", nullable = false) private Integer recommendedDurationMinutes;
    @Enumerated(EnumType.STRING) @Column(name = "recommended_intensity", nullable = false) private IntensityEnum recommendedIntensity;
    @Enumerated(EnumType.STRING) @Column(name = "recovery_suggestion", nullable = false) private RecoverySuggestionEnum recoverySuggestion;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;

    public RoutineEntity() {}
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getAthleteId() { return athleteId; } public void setAthleteId(UUID athleteId) { this.athleteId = athleteId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public Integer getRecommendedDurationMinutes() { return recommendedDurationMinutes; } public void setRecommendedDurationMinutes(Integer recommendedDurationMinutes) { this.recommendedDurationMinutes = recommendedDurationMinutes; }
    public IntensityEnum getRecommendedIntensity() { return recommendedIntensity; } public void setRecommendedIntensity(IntensityEnum recommendedIntensity) { this.recommendedIntensity = recommendedIntensity; }
    public RecoverySuggestionEnum getRecoverySuggestion() { return recoverySuggestion; } public void setRecoverySuggestion(RecoverySuggestionEnum recoverySuggestion) { this.recoverySuggestion = recoverySuggestion; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public enum IntensityEnum { LIGHT, MODERATE, HIGH, EXTREME }
    public enum RecoverySuggestionEnum { ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, MODERATE_WORKOUT, INCREASE_INTENSITY }
}