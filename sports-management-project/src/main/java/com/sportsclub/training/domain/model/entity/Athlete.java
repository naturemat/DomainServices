package com.sportsclub.training.domain.model.entity;

import com.sportsclub.training.domain.model.valueobject.SportType;
import java.time.LocalDate;
import java.util.UUID;

public class Athlete {
    private final UUID id;
    private final String name;
    private final SportType sportType;
    private final LocalDate birthDate;
    private final LocalDate createdAt;

    public Athlete(UUID id, String name, SportType sportType, LocalDate birthDate, LocalDate createdAt) {
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

    public SportType getSportType() {
        return sportType;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public int calculateAge() {
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }

    public static Athlete create(String name, SportType sportType, LocalDate birthDate) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Athlete name must not be blank");
        if (sportType == null) throw new IllegalArgumentException("Sport type must not be null");
        if (birthDate == null) throw new IllegalArgumentException("Birth date must not be null");
        if (birthDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("Birth date must be in the past");
        return new Athlete(UUID.randomUUID(), name, sportType, birthDate, LocalDate.now());
    }
}