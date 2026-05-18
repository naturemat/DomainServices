package domain.ports.out;

import domain.model.entities.TrainingSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TrainingSessionRepository {
    List<TrainingSession> findRecentByAthleteId(UUID athleteId, LocalDateTime since);
    TrainingSession save(TrainingSession session);
    List<TrainingSession> findByAthleteId(UUID athleteId);
    List<TrainingSession> findAll();
    List<TrainingSession> findByAthleteNameContaining(String name);
}