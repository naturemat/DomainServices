package domain.ports.out;

import domain.model.entities.Athlete;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AthleteRepository {
    Optional<Athlete> findById(UUID id);
    List<Athlete> findAll();
    List<Athlete> findByNameContaining(String name);
    Athlete save(Athlete athlete);
    boolean existsById(UUID id);
}