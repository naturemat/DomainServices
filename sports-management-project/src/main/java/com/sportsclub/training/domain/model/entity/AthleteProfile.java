package com.sportsclub.training.domain.model.entity;

import java.util.UUID;

public class AthleteProfile {
    private final UUID id;
    private final Athlete athlete;
    private int totalMinutesThisWeek;
    private int sessionsThisWeek;

    public AthleteProfile(UUID id, Athlete athlete) {
        this.id = id;
        this.athlete = athlete;
        this.totalMinutesThisWeek = 0;
        this.sessionsThisWeek = 0;
    }

    public boolean hasCompetitionInNextDays(int days) {
        return false;
    }

    public UUID getId() {
        return id;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public int getTotalMinutesThisWeek() {
        return totalMinutesThisWeek;
    }

    public int getSessionsThisWeek() {
        return sessionsThisWeek;
    }
}
