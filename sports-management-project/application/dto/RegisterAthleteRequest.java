package application.dto;

import domain.model.valueobjects.SportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RegisterAthleteRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Sport type is required")
    private SportType sportType;

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    public RegisterAthleteRequest() {}

    public RegisterAthleteRequest(String name, SportType sportType, LocalDate birthDate) {
        this.name = name;
        this.sportType = sportType;
        this.birthDate = birthDate;
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
}