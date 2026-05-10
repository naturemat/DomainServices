package infrastructure.persistence.postgres.repositories;

import domain.model.entities.Routine;
import domain.ports.out.RoutineRepository;
import infrastructure.persistence.postgres.entities.RoutineEntity;
import infrastructure.persistence.postgres.mappers.DomainEntityMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public class PostgresRoutineRepository implements RoutineRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final DomainEntityMapper mapper = new DomainEntityMapper();

    @Override
    public Routine save(Routine routine) {
        RoutineEntity entity = mapper.toEntity(routine);
        if (routine.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return routine;
    }

    @Override
    public List<Routine> findByAthleteId(UUID athleteId) {
        TypedQuery<RoutineEntity> query = entityManager.createQuery(
            "SELECT r FROM RoutineEntity r WHERE r.athleteId = :athleteId ORDER BY r.createdAt DESC",
            RoutineEntity.class
        );
        query.setParameter("athleteId", athleteId);
        return query.getResultList().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public Routine findLatestByAthleteId(UUID athleteId) {
        TypedQuery<RoutineEntity> query = entityManager.createQuery(
            "SELECT r FROM RoutineEntity r WHERE r.athleteId = :athleteId ORDER BY r.createdAt DESC",
            RoutineEntity.class
        );
        query.setParameter("athleteId", athleteId);
        query.setMaxResults(1);
        List<RoutineEntity> results = query.getResultList();
        return results.isEmpty() ? null : mapper.toDomain(results.get(0));
    }
}