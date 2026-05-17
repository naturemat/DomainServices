package application.usecase;

import application.dto.RegisterAthleteRequest;
import application.dto.RegisterAthleteResponse;
import domain.model.entities.Athlete;
import domain.model.valueobjects.SportType;
import domain.ports.out.AthleteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterAthleteUseCase {

    private final AthleteRepository athleteRepository;

    public RegisterAthleteUseCase(AthleteRepository athleteRepository) {
        this.athleteRepository = athleteRepository;
    }

    @Transactional
    public RegisterAthleteResponse execute(RegisterAthleteRequest request) {
        Athlete athlete = Athlete.create(
            request.getName(),
            request.getSportType(),
            request.getBirthDate()
        );

        Athlete savedAthlete = athleteRepository.save(athlete);

        return new RegisterAthleteResponse(
            savedAthlete.getId(),
            savedAthlete.getName(),
            savedAthlete.getSportType(),
            savedAthlete.getBirthDate(),
            savedAthlete.getCreatedAt()
        );
    }
}