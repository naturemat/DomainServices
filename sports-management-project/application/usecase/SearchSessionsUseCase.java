package application.usecase;

import domain.model.entities.TrainingSession;
import domain.ports.out.AthleteRepository;
import domain.ports.out.TrainingSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shared.exceptions.InvalidSearchParameterException;

import java.util.List;
import java.util.UUID;

@Service
public class SearchSessionsUseCase {

    private final TrainingSessionRepository trainingSessionRepository;
    private final AthleteRepository athleteRepository;
    private static final int MAX_SEARCH_LENGTH = 100;

    public SearchSessionsUseCase(
            TrainingSessionRepository trainingSessionRepository,
            AthleteRepository athleteRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.athleteRepository = athleteRepository;
    }

    @Transactional(readOnly = true)
    public List<TrainingSession> executeByAthleteName(String athleteName) {
        validateSearchParameter(athleteName);
        
        if (athleteName == null || athleteName.trim().isEmpty()) {
            return trainingSessionRepository.findAll();
        }
        
        return trainingSessionRepository.findByAthleteNameContaining(athleteName.trim());
    }

    @Transactional(readOnly = true)
    public List<TrainingSession> executeByAthleteId(UUID athleteId) {
        if (athleteId == null) {
            return trainingSessionRepository.findAll();
        }
        
        boolean exists = athleteRepository.existsById(athleteId);
        if (!exists) {
            return List.of();
        }
        
        return trainingSessionRepository.findByAthleteId(athleteId);
    }

    private void validateSearchParameter(String name) {
        if (name != null && name.length() > MAX_SEARCH_LENGTH) {
            throw new InvalidSearchParameterException(
                "Search query exceeds maximum length of " + MAX_SEARCH_LENGTH + " characters"
            );
        }
    }
}