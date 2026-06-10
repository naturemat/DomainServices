package com.sportsclub.infrastructure.adapter.out.persistence.mongo.repository;

import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.infrastructure.adapter.out.persistence.mongo.document.AthleteDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class MongoAthleteRepository implements AthleteRepository {
    private final MongoTemplate mongoTemplate;

    public MongoAthleteRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<Athlete> findById(UUID id) {
        AthleteDocument doc = mongoTemplate.findById(id, AthleteDocument.class);
        return Optional.ofNullable(doc).map(this::toDomain);
    }

    @Override
    public List<Athlete> findAll() {
        return mongoTemplate.findAll(AthleteDocument.class).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Athlete> findByNameContaining(String name) {
        Query query = new Query(Criteria.where("name").regex(name, "i"));
        return mongoTemplate.find(query, AthleteDocument.class).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Athlete> findTop100ByOrderByName() {
        Query query = new Query().limit(100);
        return mongoTemplate.find(query, AthleteDocument.class).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Athlete save(Athlete athlete) {
        AthleteDocument doc = toDocument(athlete);
        mongoTemplate.save(doc);
        return athlete;
    }

    @Override
    public boolean existsById(UUID id) {
        return mongoTemplate.findById(id, AthleteDocument.class) != null;
    }

    private Athlete toDomain(AthleteDocument doc) {
        return new Athlete(doc.getId(), doc.getName(),
                SportType.valueOf(doc.getSportType()),
                doc.getBirthDate(), doc.getCreatedAt());
    }

    private AthleteDocument toDocument(Athlete athlete) {
        return new AthleteDocument(athlete.getId(), athlete.getName(),
                athlete.getSportType().name(),
                athlete.getBirthDate(), athlete.getCreatedAt());
    }
}
