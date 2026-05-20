package com.sportsclub.training.application.usecase;

import com.sportsclub.training.application.dto.response.RoutineResponse;
import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.training.domain.port.out.RoutineRepository;
import com.sportsclub.training.domain.service.RoutineRecommender;
import com.sportsclub.performance.domain.port.out.FatigueMetricsRepository;
import com.sportsclub.shared.domain.model.FatigueLevel;
import com.sportsclub.shared.domain.exception.AthleteNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class GenerateRoutineUseCase {
    private final AthleteRepository athleteRepository;
    private final FatigueMetricsRepository fatigueMetricsRepository;
    private final RoutineRepository routineRepository;
    private final RoutineRecommender routineRecommender;

    public GenerateRoutineUseCase(AthleteRepository athleteRepository, FatigueMetricsRepository fatigueMetricsRepository, RoutineRepository routineRepository, RoutineRecommender routineRecommender) {
        this.athleteRepository = athleteRepository; this.fatigueMetricsRepository = fatigueMetricsRepository; this.routineRepository = routineRepository; this.routineRecommender = routineRecommender;
    }

    @Transactional
    public RoutineResponse execute(UUID athleteId) {
        Athlete athlete = athleteRepository.findById(athleteId).orElseThrow(() -> new AthleteNotFoundException("Athlete not found: " + athleteId));
        FatigueLevel currentFatigue = fatigueMetricsRepository.findLatestByAthleteId(athleteId).orElse(FatigueLevel.LOW);
        Routine recommendedRoutine = routineRecommender.recommendRoutine(athleteId, currentFatigue, athlete.getSportType());
        routineRepository.save(recommendedRoutine);
        return RoutineResponse.fromDomain(recommendedRoutine, athlete.getName());
    }
}