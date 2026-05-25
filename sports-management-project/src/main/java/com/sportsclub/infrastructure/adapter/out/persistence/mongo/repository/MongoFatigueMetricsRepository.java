package com.sportsclub.infrastructure.adapter.out.persistence.mongo.repository;

import com.sportsclub.performance.domain.model.entity.FatigueMetrics;
import com.sportsclub.performance.domain.port.out.FatigueMetricsRepository;
import com.sportsclub.shared.domain.model.FatigueLevel;
import com.sportsclub.infrastructure.adapter.out.persistence.mongo.document.FatigueMetricsDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MongoFatigueMetricsRepository implements FatigueMetricsRepository {
    private final MongoTemplate mongoTemplate;

    public MongoFatigueMetricsRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UUID athleteId, FatigueLevel fatigueLevel, LocalDateTime calculatedAt) {
        mongoTemplate.save(new FatigueMetricsDocument(athleteId, fatigueLevel.name(), calculatedAt));
    }

    @Override
    public Optional<FatigueLevel> findLatestByAthleteId(UUID athleteId) {
        Query query = new Query().addCriteria(Criteria.where("athleteId").is(athleteId)).limit(1);
        FatigueMetricsDocument doc = mongoTemplate.findOne(query, FatigueMetricsDocument.class);
        return doc == null ? Optional.empty() : Optional.of(FatigueLevel.valueOf(doc.getFatigueLevel()));
    }

    @Override
    public List<FatigueMetrics> findByAthleteId(UUID athleteId) {
        Query query = new Query().addCriteria(Criteria.where("athleteId").is(athleteId));
        return mongoTemplate.find(query, FatigueMetricsDocument.class).stream()
                .map(doc -> new FatigueMetrics(UUID.fromString(doc.getId()), athleteId,
                        FatigueLevel.valueOf(doc.getFatigueLevel()), doc.getCalculatedAt()))
                .toList();
    }

    @Override
    public List<FatigueMetrics> findByAthleteIdAndDateRange(UUID athleteId, LocalDateTime start, LocalDateTime end) {
        Query query = new Query()
                .addCriteria(Criteria.where("athleteId").is(athleteId).and("calculatedAt").gte(start).lte(end));
        return mongoTemplate.find(query, FatigueMetricsDocument.class).stream()
                .map(doc -> new FatigueMetrics(UUID.fromString(doc.getId()), athleteId,
                        FatigueLevel.valueOf(doc.getFatigueLevel()), doc.getCalculatedAt()))
                .toList();
    }
}