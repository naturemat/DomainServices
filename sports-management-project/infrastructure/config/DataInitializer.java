package infrastructure.config;

import domain.model.entities.Athlete;
import domain.model.entities.TrainingSession;
import domain.model.valueobjects.Intensity;
import domain.ports.out.AthleteRepository;
import domain.ports.out.TrainingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final AthleteRepository athleteRepository;
    private final TrainingSessionRepository trainingSessionRepository;

    public DataInitializer(AthleteRepository athleteRepository,
                          TrainingSessionRepository trainingSessionRepository) {
        this.athleteRepository = athleteRepository;
        this.trainingSessionRepository = trainingSessionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("=== INITIALIZING DATABASE WITH SAMPLE DATA ===");

        Athlete athlete1 = Athlete.create(
            "Juan Pérez",
            domain.model.valueobjects.SportType.GYM,
            java.time.LocalDate.of(1995, 5, 15)
        );
        if (!athleteRepository.existsById(athlete1.getId())) {
            athleteRepository.save(athlete1);
            logger.info("Created athlete: {}", athlete1.getName());
            createTrainingSessions(athlete1);
        } else {
            logger.info("Athlete {} already exists", athlete1.getName());
        }

        Athlete athlete2 = Athlete.create(
            "María García",
            domain.model.valueobjects.SportType.FOOTBALL,
            java.time.LocalDate.of(1998, 8, 22)
        );
        if (!athleteRepository.existsById(athlete2.getId())) {
            athleteRepository.save(athlete2);
            logger.info("Created athlete: {}", athlete2.getName());
            createTrainingSessions(athlete2);
        } else {
            logger.info("Athlete {} already exists", athlete2.getName());
        }

        logger.info("=== INITIAL DATA LOADED SUCCESSFULLY ===");
    }

    private void createTrainingSessions(Athlete athlete) {
        LocalDateTime now = LocalDateTime.now();

        TrainingSession session1 = TrainingSession.create(
            athlete.getId(),
            now.minusDays(1),
            60,
            Intensity.HIGH,
            500
        );
        trainingSessionRepository.save(session1);

        TrainingSession session2 = TrainingSession.create(
            athlete.getId(),
            now.minusDays(2),
            45,
            Intensity.MODERATE,
            300
        );
        trainingSessionRepository.save(session2);

        TrainingSession session3 = TrainingSession.create(
            athlete.getId(),
            now.minusDays(3),
            30,
            Intensity.LIGHT,
            150
        );
        trainingSessionRepository.save(session3);

        logger.info("Created 3 training sessions for athlete: {}", athlete.getName());
    }
}