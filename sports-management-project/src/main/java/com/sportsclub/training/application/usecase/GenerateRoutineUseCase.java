package com.sportsclub.training.application.usecase;

import com.sportsclub.training.application.dto.response.RoutineResponse;
import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.training.domain.port.out.RoutineRepository;
import com.sportsclub.training.domain.service.RoutineRecommender;
import com.sportsclub.training.domain.port.out.TrainingSessionRepository;
import com.sportsclub.training.domain.service.FatigueCalculator;
import com.sportsclub.training.domain.model.entity.TrainingSession;
import java.time.LocalDateTime;
import java.util.List;
import com.sportsclub.shared.domain.model.FatigueLevel;
import com.sportsclub.shared.domain.exception.AthleteNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class GenerateRoutineUseCase {
    private final AthleteRepository athleteRepository;
    private final RoutineRepository routineRepository;
    private final RoutineRecommender routineRecommender;
    private final TrainingSessionRepository trainingSessionRepository;
    private final FatigueCalculator fatigueCalculator;

    public GenerateRoutineUseCase(AthleteRepository athleteRepository, RoutineRepository routineRepository,
            RoutineRecommender routineRecommender, TrainingSessionRepository trainingSessionRepository,
            FatigueCalculator fatigueCalculator) {
        this.athleteRepository = athleteRepository;
        this.routineRepository = routineRepository;
        this.routineRecommender = routineRecommender;
        this.trainingSessionRepository = trainingSessionRepository;
        this.fatigueCalculator = fatigueCalculator;
    }

    @Transactional
    public RoutineResponse execute(UUID athleteId) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new AthleteNotFoundException("Athlete not found: " + athleteId));
        List<TrainingSession> recentSessions = trainingSessionRepository.findRecentByAthleteId(athleteId,
                LocalDateTime.now().minusHours(72));
        FatigueLevel currentFatigue = fatigueCalculator.calculateFatigue(recentSessions, LocalDateTime.now());
        Routine recommendedRoutine = routineRecommender.recommendRoutine(athleteId, athlete.getSportType());
        routineRepository.save(recommendedRoutine);
        return RoutineResponse.fromDomain(recommendedRoutine, athlete.getName());
    }
}