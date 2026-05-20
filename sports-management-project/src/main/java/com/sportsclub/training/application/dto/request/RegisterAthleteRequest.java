package com.sportsclub.training.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RegisterAthleteRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Sport type is required")
    private String sportType;
    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    public RegisterAthleteRequest() {}
    public RegisterAthleteRequest(String name, String sportType, LocalDate birthDate) { this.name = name; this.sportType = sportType; this.birthDate = birthDate; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}