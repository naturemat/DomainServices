package com.sportsclub.infrastructure.config;

import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.training.domain.port.out.TrainingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final AthleteRepository athleteRepository;
    private final TrainingSessionRepository trainingSessionRepository;

    public DataInitializer(AthleteRepository athleteRepository, TrainingSessionRepository trainingSessionRepository) {
        this.athleteRepository = athleteRepository; this.trainingSessionRepository = trainingSessionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("=== INITIALIZING DATABASE WITH SAMPLE DATA ===");
        Athlete athlete1 = Athlete.create("Juan Pérez", SportType.GYM, java.time.LocalDate.of(1995, 5, 15));
        if (!athleteRepository.existsById(athlete1.getId())) { athleteRepository.save(athlete1); logger.info("Created athlete: {}", athlete1.getName()); createTrainingSessions(athlete1); }
        else { logger.info("Athlete {} already exists", athlete1.getName()); }

        Athlete athlete2 = Athlete.create("María García", SportType.FOOTBALL, java.time.LocalDate.of(1998, 8, 22));
        if (!athleteRepository.existsById(athlete2.getId())) { athleteRepository.save(athlete2); logger.info("Created athlete: {}", athlete2.getName()); createTrainingSessions(athlete2); }
        else { logger.info("Athlete {} already exists", athlete2.getName()); }
        logger.info("=== INITIAL DATA LOADED SUCCESSFULLY ===");
    }

    private void createTrainingSessions(Athlete athlete) {
        LocalDateTime now = LocalDateTime.now();
        trainingSessionRepository.save(TrainingSession.create(athlete.getId(), now.minusDays(1), 60, Intensity.HIGH, 500));
        trainingSessionRepository.save(TrainingSession.create(athlete.getId(), now.minusDays(2), 45, Intensity.MODERATE, 300));
        trainingSessionRepository.save(TrainingSession.create(athlete.getId(), now.minusDays(3), 30, Intensity.LIGHT, 150));
        logger.info("Created 3 training sessions for athlete: {}", athlete.getName());
    }
}