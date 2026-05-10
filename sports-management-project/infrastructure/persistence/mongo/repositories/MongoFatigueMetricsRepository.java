package infrastructure.persistence.mongo.repositories;

import domain.model.valueobjects.FatigueLevel;
import domain.ports.out.FatigueMetricsRepository;
import infrastructure.persistence.mongo.documents.FatigueMetricsDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
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
        FatigueMetricsDocument document = new FatigueMetricsDocument(
            athleteId,
            fatigueLevel.name(),
            calculatedAt
        );
        mongoTemplate.save(document);
    }

    @Override
    public Optional<FatigueLevel> findLatestByAthleteId(UUID athleteId) {
        Query query = new Query()
            .addCriteria(Criteria.where("athleteId").is(athleteId))
            .limit(1);

        FatigueMetricsDocument document = mongoTemplate.findOne(query, FatigueMetricsDocument.class);

        if (document == null) {
            return Optional.empty();
        }

        return Optional.of(FatigueLevel.valueOf(document.getFatigueLevel()));
    }
}