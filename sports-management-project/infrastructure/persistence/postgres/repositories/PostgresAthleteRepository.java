package infrastructure.persistence.postgres.repositories;

import domain.model.entities.Athlete;
import domain.ports.out.AthleteRepository;
import infrastructure.persistence.postgres.entities.AthleteEntity;
import infrastructure.persistence.postgres.mappers.DomainEntityMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresAthleteRepository implements AthleteRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final DomainEntityMapper mapper = new DomainEntityMapper();

    @Override
    public Optional<Athlete> findById(UUID id) {
        AthleteEntity entity = entityManager.find(AthleteEntity.class, id);
        return Optional.ofNullable(mapper.toDomain(entity));
    }

    @Override
    public Athlete save(Athlete athlete) {
        if (athlete.getId() == null) {
            entityManager.persist(mapper.toEntity(athlete));
            return athlete;
        } else {
            entityManager.merge(mapper.toEntity(athlete));
            return athlete;
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return entityManager.find(AthleteEntity.class, id) != null;
    }
}