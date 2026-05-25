package com.sportsclub.training.application.usecase;

import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.shared.domain.exception.InvalidSearchParameterException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        if (name != null && name.length() > MAX_SEARCH_LENGTH)
            throw new InvalidSearchParameterException(
                    "Search query exceeds maximum length of " + MAX_SEARCH_LENGTH + " characters");
        if (name == null || name.trim().isEmpty())
            return athleteRepository.findTop100ByOrderByName();
        return athleteRepository.findByNameContaining(name.trim());
    }
}