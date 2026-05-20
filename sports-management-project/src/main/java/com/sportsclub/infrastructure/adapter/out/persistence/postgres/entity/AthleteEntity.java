package com.sportsclub.infrastructure.adapter.out.persistence.postgres.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "athletes")
public class AthleteEntity {
    @Id private UUID id;
    @Column(name = "name", nullable = false) private String name;
    @Enumerated(EnumType.STRING) @Column(name = "sport_type", nullable = false) private SportTypeEnum sportType;
    @Column(name = "birth_date") private LocalDate birthDate;
    @Column(name = "created_at", nullable = false) private LocalDate createdAt;

    public AthleteEntity() {}
    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public SportTypeEnum getSportType() { return sportType; } public void setSportType(SportTypeEnum sportType) { this.sportType = sportType; }
    public LocalDate getBirthDate() { return birthDate; } public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public LocalDate getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public enum SportTypeEnum { GYM, FOOTBALL }
}