package com.sportsclub.training.application.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public class RegisterAthleteResponse {
    private final UUID id;
    private final String name;
    private final String sportType;
    private final LocalDate birthDate;
    private final LocalDate createdAt;

    public RegisterAthleteResponse(UUID id, String name, String sportType, LocalDate birthDate, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.sportType = sportType;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSportType() {
        return sportType;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}