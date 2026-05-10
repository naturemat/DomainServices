package domain.ports.out;

import domain.model.entities.Routine;
import java.util.List;
import java.util.UUID;

public interface RoutineRepository {
    Routine save(Routine routine);
    List<Routine> findByAthleteId(UUID athleteId);
    Routine findLatestByAthleteId(UUID athleteId);
}