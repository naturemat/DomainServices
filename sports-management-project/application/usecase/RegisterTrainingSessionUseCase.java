package application.usecase;

import application.dto.RegisterSessionRequest;
import application.dto.RegisterSessionResponse;
import domain.model.entities.Athlete;
import domain.model.entities.Routine;
import domain.model.entities.TrainingSession;
import domain.model.enums.RecoverySuggestion;
import domain.model.valueobjects.FatigueLevel;
import domain.model.valueobjects.Intensity;
import domain.policies.FatigueRules;
import domain.ports.out.AthleteRepository;
import domain.ports.out.FatigueMetricsRepository;
import domain.ports.out.RoutineRepository;
import domain.ports.out.TrainingSessionRepository;
import domain.services.FatigueCalculationService;
import domain.services.RecoverySuggestionService;
import domain.services.RoutineRecommendationService;
import shared.exceptions.AthleteNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RegisterTrainingSessionUseCase {

    private final AthleteRepository athleteRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final RoutineRepository routineRepository;
    private final FatigueMetricsRepository fatigueMetricsRepository;
    private final FatigueCalculationService fatigueCalculationService;
    private final RoutineRecommendationService routineRecommendationService;
    private final RecoverySuggestionService recoverySuggestionService;
    private final FatigueRules fatigueRules;

    public RegisterTrainingSessionUseCase(
            AthleteRepository athleteRepository,
            TrainingSessionRepository trainingSessionRepository,
            RoutineRepository routineRepository,
            FatigueMetricsRepository fatigueMetricsRepository,
            FatigueCalculationService fatigueCalculationService,
            RoutineRecommendationService routineRecommendationService,
            RecoverySuggestionService recoverySuggestionService,
            FatigueRules fatigueRules) {
        this.athleteRepository = athleteRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.routineRepository = routineRepository;
        this.fatigueMetricsRepository = fatigueMetricsRepository;
        this.fatigueCalculationService = fatigueCalculationService;
        this.routineRecommendationService = routineRecommendationService;
        this.recoverySuggestionService = recoverySuggestionService;
        this.fatigueRules = fatigueRules;
    }

    public RegisterSessionResponse execute(RegisterSessionRequest request) {
        UUID athleteId = request.getAthleteId();

        Athlete athlete = athleteRepository.findById(athleteId)
            .orElseThrow(() -> new AthleteNotFoundException("Athlete not found: " + athleteId));

        LocalDateTime sessionDate = request.getSessionDate();
        int durationMinutes = request.getDurationMinutes();
        Intensity intensity = request.getIntensity();
        int caloriesBurned = request.getCaloriesBurned() != null
            ? request.getCaloriesBurned()
            : estimateCalories(durationMinutes, intensity);

        TrainingSession session = TrainingSession.create(
            athleteId, sessionDate, durationMinutes, intensity, caloriesBurned
        );

        LocalDateTime windowStart = sessionDate.minusHours(fatigueRules.getRecoveryWindowHours());
        List<TrainingSession> recentSessions = trainingSessionRepository.findRecentByAthleteId(athleteId, windowStart);

        FatigueLevel fatigueLevel = fatigueCalculationService.calculateFatigue(recentSessions, sessionDate);

        Routine recommendedRoutine = routineRecommendationService.recommendRoutine(
            athleteId, fatigueLevel, athlete.getSportType()
        );

        RecoverySuggestion recoverySuggestion = recoverySuggestionService.getSuggestion(
            fatigueLevel, athlete.getSportType()
        );

        trainingSessionRepository.save(session);
        routineRepository.save(recommendedRoutine);
        fatigueMetricsRepository.save(athleteId, fatigueLevel, LocalDateTime.now());

        return new RegisterSessionResponse(
            session.getSessionId(),
            athlete.getId(),
            session.getSessionDate(),
            session.getDurationMinutes(),
            session.getIntensity(),
            fatigueLevel,
            recommendedRoutine,
            recoverySuggestion,
            session.getCreatedAt()
        );
    }

    private int estimateCalories(int durationMinutes, Intensity intensity) {
        int baseCaloriesPerMinute = 5;
        double intensityMultiplier = switch (intensity) {
            case LIGHT -> 1.0;
            case MODERATE -> 1.5;
            case HIGH -> 2.0;
            case EXTREME -> 2.5;
        };
        return (int) (durationMinutes * baseCaloriesPerMinute * intensityMultiplier);
    }
}