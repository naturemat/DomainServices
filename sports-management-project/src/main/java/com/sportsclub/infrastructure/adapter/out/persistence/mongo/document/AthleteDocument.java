package com.sportsclub.infrastructure.adapter.out.persistence.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "athletes")
public class AthleteDocument {
    @Id
    private UUID id;
    private String name;
    private String sportType;
    private LocalDate birthDate;
    private LocalDate createdAt;

    public AthleteDocument() {
    }

    public AthleteDocument(UUID id, String name, String sportType, LocalDate birthDate, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.sportType = sportType;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
