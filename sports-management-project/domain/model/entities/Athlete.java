package domain.model.entities;

import domain.model.valueobjects.SportType;
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

    public static Athlete create(String name, SportType sportType, LocalDate birthDate) {
        return new Athlete(
            UUID.randomUUID(),
            name,
            sportType,
            birthDate,
            LocalDate.now()
        );
    }
}