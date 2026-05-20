package com.sportsclub.training.application.usecase;

import com.sportsclub.training.application.dto.request.RegisterSessionRequest;
import com.sportsclub.training.application.dto.response.RegisterSessionResponse;
import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.model.enums.RecoverySuggestion;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.policy.FatigueRules;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.training.domain.port.out.RoutineRepository;
import com.sportsclub.training.domain.port.out.TrainingSessionRepository;
import com.sportsclub.training.domain.service.FatigueCalculator;
import com.sportsclub.training.domain.service.RecoverySuggester;
import com.sportsclub.training.domain.service.RoutineRecommender;
import com.sportsclub.performance.domain.port.out.FatigueMetricsRepository;
import com.sportsclub.shared.domain.model.FatigueLevel;
import com.sportsclub.shared.domain.exception.AthleteNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RegisterTrainingSessionUseCase {
    private final AthleteRepository athleteRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final RoutineRepository routineRepository;
    private final FatigueMetricsRepository fatigueMetricsRepository;
    private final FatigueCalculator fatigueCalculator;
    private final RoutineRecommender routineRecommender;
    private final RecoverySuggester recoverySuggester;
    private final FatigueRules fatigueRules;

    public RegisterTrainingSessionUseCase(AthleteRepository athleteRepository, TrainingSessionRepository trainingSessionRepository, RoutineRepository routineRepository, FatigueMetricsRepository fatigueMetricsRepository, FatigueCalculator fatigueCalculator, RoutineRecommender routineRecommender, RecoverySuggester recoverySuggester, FatigueRules fatigueRules) {
        this.athleteRepository = athleteRepository; this.trainingSessionRepository = trainingSessionRepository; this.routineRepository = routineRepository;
        this.fatigueMetricsRepository = fatigueMetricsRepository; this.fatigueCalculator = fatigueCalculator;
        this.routineRecommender = routineRecommender; this.recoverySuggester = recoverySuggester; this.fatigueRules = fatigueRules;
    }

    @Transactional
    public RegisterSessionResponse execute(RegisterSessionRequest request) {
        UUID athleteId = request.getAthleteId();
        Athlete athlete = athleteRepository.findById(athleteId).orElseThrow(() -> new AthleteNotFoundException("Athlete not found: " + athleteId));
        LocalDateTime sessionDate = request.getSessionDate();
        int durationMinutes = request.getDurationMinutes();
        Intensity intensity = request.getIntensityEnum();
        int caloriesBurned = request.getCaloriesBurned() != null ? request.getCaloriesBurned() : (int)(durationMinutes * 5 * (intensity == Intensity.LIGHT ? 1.0 : intensity == Intensity.MODERATE ? 1.5 : intensity == Intensity.HIGH ? 2.0 : 2.5));

        TrainingSession session = TrainingSession.create(athleteId, sessionDate, durationMinutes, intensity, caloriesBurned);
        List<TrainingSession> recentSessions = trainingSessionRepository.findRecentByAthleteId(athleteId, sessionDate.minusHours(fatigueRules.getRecoveryWindowHours()));
        FatigueLevel fatigueLevel = fatigueCalculator.calculateFatigue(recentSessions, sessionDate);
        Routine recommendedRoutine = routineRecommender.recommendRoutine(athleteId, fatigueLevel, athlete.getSportType());
        RecoverySuggestion recoverySuggestion = recoverySuggester.getSuggestion(fatigueLevel, athlete.getSportType());

        trainingSessionRepository.save(session);
        routineRepository.save(recommendedRoutine);
        fatigueMetricsRepository.save(athleteId, fatigueLevel, LocalDateTime.now());

        return new RegisterSessionResponse(session.getSessionId(), athlete.getId(), session.getSessionDate(), session.getDurationMinutes(), session.getIntensity(), fatigueLevel, recommendedRoutine, recoverySuggestion, session.getCreatedAt());
    }
}