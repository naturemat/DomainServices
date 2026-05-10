package domain.ports.out;

import domain.model.entities.Athlete;
import java.util.Optional;
import java.util.UUID;

public interface AthleteRepository {
    Optional<Athlete> findById(UUID id);
    Athlete save(Athlete athlete);
    boolean existsById(UUID id);
}