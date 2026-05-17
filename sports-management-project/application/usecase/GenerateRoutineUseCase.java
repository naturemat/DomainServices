package application.usecase;

import application.dto.RoutineResponse;
import domain.model.entities.Athlete;
import domain.model.entities.Routine;
import domain.model.valueobjects.FatigueLevel;
import domain.ports.out.AthleteRepository;
import domain.ports.out.FatigueMetricsRepository;
import domain.ports.out.RoutineRepository;
import domain.services.RoutineRecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shared.exceptions.AthleteNotFoundException;
import java.util.UUID;

@Service
public class GenerateRoutineUseCase {

    private final AthleteRepository athleteRepository;
    private final FatigueMetricsRepository fatigueMetricsRepository;
    private final RoutineRepository routineRepository;
    private final RoutineRecommendationService routineRecommendationService;

    public GenerateRoutineUseCase(
            AthleteRepository athleteRepository,
            FatigueMetricsRepository fatigueMetricsRepository,
            RoutineRepository routineRepository,
            RoutineRecommendationService routineRecommendationService) {
        this.athleteRepository = athleteRepository;
        this.fatigueMetricsRepository = fatigueMetricsRepository;
        this.routineRepository = routineRepository;
        this.routineRecommendationService = routineRecommendationService;
    }

    @Transactional
    public RoutineResponse execute(UUID athleteId) {
        Athlete athlete = athleteRepository.findById(athleteId)
            .orElseThrow(() -> new AthleteNotFoundException("Athlete not found: " + athleteId));

        FatigueLevel currentFatigue = fatigueMetricsRepository.findLatestByAthleteId(athleteId)
            .orElse(FatigueLevel.LOW);

        Routine recommendedRoutine = routineRecommendationService.recommendRoutine(
            athleteId, currentFatigue, athlete.getSportType()
        );

        routineRepository.save(recommendedRoutine);

        return RoutineResponse.fromDomain(recommendedRoutine, athlete.getName());
    }
}