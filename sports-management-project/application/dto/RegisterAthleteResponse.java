package application.dto;

import domain.model.valueobjects.SportType;
import java.time.LocalDate;
import java.util.UUID;

public class RegisterAthleteResponse {
    private UUID id;
    private String name;
    private SportType sportType;
    private LocalDate birthDate;
    private LocalDate createdAt;

    public RegisterAthleteResponse() {}

    public RegisterAthleteResponse(UUID id, String name, SportType sportType, 
                                   LocalDate birthDate, LocalDate createdAt) {
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

    public SportType getSportType() {
        return sportType;
    }

    public void setSportType(SportType sportType) {
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