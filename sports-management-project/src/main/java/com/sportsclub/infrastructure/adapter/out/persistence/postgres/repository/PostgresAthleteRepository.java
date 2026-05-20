package com.sportsclub.infrastructure.adapter.out.persistence.postgres.repository;

import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.infrastructure.adapter.out.persistence.postgres.entity.AthleteEntity;
import com.sportsclub.infrastructure.adapter.out.persistence.postgres.mapper.DomainEntityMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresAthleteRepository implements AthleteRepository {
    @PersistenceContext private EntityManager entityManager;
    private final DomainEntityMapper mapper = new DomainEntityMapper();

    @Override
    public Optional<Athlete> findById(UUID id) { AthleteEntity entity = entityManager.find(AthleteEntity.class, id); return Optional.ofNullable(mapper.toDomain(entity)); }

    @Override
    @SuppressWarnings("unchecked")
    public List<Athlete> findAll() { Query query = entityManager.createQuery("SELECT a FROM AthleteEntity a"); return query.getResultList().stream().map(e -> mapper.toDomain((AthleteEntity) e)).toList(); }

    @Override
    @SuppressWarnings("unchecked")
    public List<Athlete> findByNameContaining(String name) { Query query = entityManager.createQuery("SELECT a FROM AthleteEntity a WHERE LOWER(a.name) LIKE LOWER(:name)"); query.setParameter("name", "%" + name + "%"); return query.getResultList().stream().map(e -> mapper.toDomain((AthleteEntity) e)).toList(); }

    @Override
    public Athlete save(Athlete athlete) { if (athlete.getId() == null) { entityManager.persist(mapper.toEntity(athlete)); return athlete; } else { entityManager.merge(mapper.toEntity(athlete)); return athlete; } }

    @Override
    public boolean existsById(UUID id) { return entityManager.find(AthleteEntity.class, id) != null; }
}