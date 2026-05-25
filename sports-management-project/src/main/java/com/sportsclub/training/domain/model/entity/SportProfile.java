package com.sportsclub.training.domain.model.entity;

import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.shared.domain.model.FatigueLevel;
import java.util.UUID;

public class SportProfile {
    private final UUID id;
    private final UUID athleteId;
    private final SportType sportType;
    private final FatigueLevel currentFatigueLevel;
    private final int totalSessionsThisWeek;
    private final int totalMinutesThisWeek;

    public SportProfile(UUID id, UUID athleteId, SportType sportType,
            FatigueLevel currentFatigueLevel, int totalSessionsThisWeek, int totalMinutesThisWeek) {
        this.id = id;
        this.athleteId = athleteId;
        this.sportType = sportType;
        this.currentFatigueLevel = currentFatigueLevel;
        this.totalSessionsThisWeek = totalSessionsThisWeek;
        this.totalMinutesThisWeek = totalMinutesThisWeek;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAthleteId() {
        return athleteId;
    }

    public SportType getSportType() {
        return sportType;
    }

    public FatigueLevel getCurrentFatigueLevel() {
        return currentFatigueLevel;
    }

    public int getTotalSessionsThisWeek() {
        return totalSessionsThisWeek;
    }

    public int getTotalMinutesThisWeek() {
        return totalMinutesThisWeek;
    }

    public SportProfile withUpdatedFatigue(FatigueLevel newFatigueLevel) {
        return new SportProfile(id, athleteId, sportType, newFatigueLevel, totalSessionsThisWeek, totalMinutesThisWeek);
    }
}