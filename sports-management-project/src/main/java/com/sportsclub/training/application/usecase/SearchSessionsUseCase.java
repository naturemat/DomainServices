package com.sportsclub.training.application.usecase;

import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.training.domain.port.out.TrainingSessionRepository;
import com.sportsclub.training.domain.exception.InvalidSearchParameterException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SearchSessionsUseCase {
    private final TrainingSessionRepository trainingSessionRepository;
    private final AthleteRepository athleteRepository;
    private static final int MAX_SEARCH_LENGTH = 100;

    public SearchSessionsUseCase(TrainingSessionRepository trainingSessionRepository,
            AthleteRepository athleteRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.athleteRepository = athleteRepository;
    }

    @Transactional(readOnly = true)
    public List<TrainingSession> executeByAthleteName(String athleteName) {
        if (athleteName != null && athleteName.length() > MAX_SEARCH_LENGTH) {
            throw new InvalidSearchParameterException("Search query exceeds maximum length");
        }
        if (athleteName == null || athleteName.trim().isEmpty()) {
            return trainingSessionRepository.findTop50ByOrderBySessionDateDesc();
        }
        List<Athlete> athletes = athleteRepository.findByNameContaining(athleteName.trim());
        if (athletes.isEmpty())
            return List.of();
        List<UUID> athleteIds = athletes.stream().map(Athlete::getId).collect(Collectors.toList());
        return trainingSessionRepository.findByAthleteIds(athleteIds);
    }

    @Transactional(readOnly = true)
    public List<TrainingSession> executeByAthleteId(UUID athleteId) {
        if (athleteId == null)
            return trainingSessionRepository.findAll();
        if (!athleteRepository.existsById(athleteId))
            return List.of();
        return trainingSessionRepository.findByAthleteId(athleteId);
    }
}