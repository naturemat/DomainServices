package application.usecase;

import domain.model.entities.Athlete;
import domain.ports.out.AthleteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shared.exceptions.InvalidSearchParameterException;

import java.util.List;

@Service
public class SearchAthletesUseCase {

    private final AthleteRepository athleteRepository;
    private static final int MAX_SEARCH_LENGTH = 100;

    public SearchAthletesUseCase(AthleteRepository athleteRepository) {
        this.athleteRepository = athleteRepository;
    }

    @Transactional(readOnly = true)
    public List<Athlete> execute(String name) {
        validateSearchParameter(name);
        
        if (name == null || name.trim().isEmpty()) {
            return athleteRepository.findAll();
        }
        
        return athleteRepository.findByNameContaining(name.trim());
    }

    private void validateSearchParameter(String name) {
        if (name != null && name.length() > MAX_SEARCH_LENGTH) {
            throw new InvalidSearchParameterException(
                "Search query exceeds maximum length of " + MAX_SEARCH_LENGTH + " characters"
            );
        }
    }
}