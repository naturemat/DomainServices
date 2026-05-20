package com.sportsclub.infrastructure.adapter.out.persistence.postgres.repository;

import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.port.out.TrainingSessionRepository;
import com.sportsclub.infrastructure.adapter.out.persistence.postgres.entity.TrainingSessionEntity;
import com.sportsclub.infrastructure.adapter.out.persistence.postgres.mapper.DomainEntityMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class PostgresTrainingSessionRepository implements TrainingSessionRepository {
    @PersistenceContext private EntityManager entityManager;
    private final DomainEntityMapper mapper = new DomainEntityMapper();

    @Override
    public List<TrainingSession> findRecentByAthleteId(UUID athleteId, LocalDateTime since) { TypedQuery<TrainingSessionEntity> query = entityManager.createQuery("SELECT s FROM TrainingSessionEntity s WHERE s.athleteId = :athleteId AND s.sessionDate >= :since", TrainingSessionEntity.class); query.setParameter("athleteId", athleteId); query.setParameter("since", since); return mapper.toDomainSessionList(query.getResultList()); }

    @Override
    public TrainingSession save(TrainingSession session) { TrainingSessionEntity entity = mapper.toEntity(session); if (session.getSessionId().getValue() == null) entityManager.persist(entity); else entityManager.merge(entity); return session; }

    @Override
    public List<TrainingSession> findByAthleteId(UUID athleteId) { TypedQuery<TrainingSessionEntity> query = entityManager.createQuery("SELECT s FROM TrainingSessionEntity s WHERE s.athleteId = :athleteId ORDER BY s.sessionDate DESC", TrainingSessionEntity.class); query.setParameter("athleteId", athleteId); return mapper.toDomainSessionList(query.getResultList()); }

    @Override
    public List<TrainingSession> findAll() { TypedQuery<TrainingSessionEntity> query = entityManager.createQuery("SELECT s FROM TrainingSessionEntity s ORDER BY s.sessionDate DESC", TrainingSessionEntity.class); return mapper.toDomainSessionList(query.getResultList()); }

    @Override
    public List<TrainingSession> findByAthleteNameContaining(String name) { TypedQuery<TrainingSessionEntity> query = entityManager.createQuery("SELECT s FROM TrainingSessionEntity s JOIN AthleteEntity a ON s.athleteId = a.id WHERE LOWER(a.name) LIKE LOWER(:name) ORDER BY s.sessionDate DESC", TrainingSessionEntity.class); query.setParameter("name", "%" + name + "%"); return mapper.toDomainSessionList(query.getResultList()); }
}