package com.sportsclub.training.application.usecase;

import com.sportsclub.training.application.dto.request.RegisterAthleteRequest;
import com.sportsclub.training.application.dto.response.RegisterAthleteResponse;
import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterAthleteUseCase {
    private final AthleteRepository athleteRepository;
    public RegisterAthleteUseCase(AthleteRepository athleteRepository) { this.athleteRepository = athleteRepository; }

    @Transactional
    public RegisterAthleteResponse execute(RegisterAthleteRequest request) {
        Athlete athlete = Athlete.create(request.getName(), SportType.valueOf(request.getSportType()), request.getBirthDate());
        Athlete savedAthlete = athleteRepository.save(athlete);
        return new RegisterAthleteResponse(savedAthlete.getId(), savedAthlete.getName(), savedAthlete.getSportType().name(), savedAthlete.getBirthDate(), savedAthlete.getCreatedAt());
    }
}