package infrastructure.persistence.postgres.mappers;

import domain.model.entities.Athlete;
import domain.model.entities.Routine;
import domain.model.entities.TrainingSession;
import domain.model.enums.RecoverySuggestion;
import domain.model.valueobjects.Intensity;
import domain.model.valueobjects.SportType;
import domain.model.valueobjects.SessionId;
import infrastructure.persistence.postgres.entities.AthleteEntity;
import infrastructure.persistence.postgres.entities.RoutineEntity;
import infrastructure.persistence.postgres.entities.TrainingSessionEntity;
import java.util.List;
import java.util.stream.Collectors;

public class DomainEntityMapper {

    public Athlete toDomain(AthleteEntity entity) {
        if (entity == null) return null;
        return new Athlete(
            entity.getId(),
            entity.getName(),
            mapSportType(entity.getSportType()),
            entity.getBirthDate(),
            entity.getCreatedAt()
        );
    }

    public AthleteEntity toEntity(Athlete domain) {
        if (domain == null) return null;
        AthleteEntity entity = new AthleteEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setSportType(mapSportTypeToEnum(domain.getSportType()));
        entity.setBirthDate(domain.getBirthDate());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public TrainingSession toDomain(TrainingSessionEntity entity) {
        if (entity == null) return null;
        return new TrainingSession(
            SessionId.from(entity.getSessionId()),
            entity.getAthleteId(),
            entity.getSessionDate(),
            entity.getDurationMinutes(),
            mapIntensity(entity.getIntensity()),
            entity.getCaloriesBurned(),
            entity.getCreatedAt()
        );
    }

    public TrainingSessionEntity toEntity(TrainingSession domain) {
        if (domain == null) return null;
        TrainingSessionEntity entity = new TrainingSessionEntity();
        entity.setSessionId(domain.getSessionId().getValue());
        entity.setAthleteId(domain.getAthleteId());
        entity.setSessionDate(domain.getSessionDate());
        entity.setDurationMinutes(domain.getDurationMinutes());
        entity.setIntensity(mapIntensityToEnum(domain.getIntensity()));
        entity.setCaloriesBurned(domain.getCaloriesBurned());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public List<TrainingSession> toDomainSessionList(List<TrainingSessionEntity> entities) {
        if (entities == null) return null;
        return entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public Routine toDomain(RoutineEntity entity) {
        if (entity == null) return null;
        return new Routine(
            entity.getId(),
            entity.getAthleteId(),
            entity.getName(),
            entity.getDescription(),
            entity.getRecommendedDurationMinutes(),
            mapRoutineIntensity(entity.getRecommendedIntensity()),
            mapRecoverySuggestion(entity.getRecoverySuggestion()),
            entity.getCreatedAt()
        );
    }

    public RoutineEntity toEntity(Routine domain) {
        if (domain == null) return null;
        RoutineEntity entity = new RoutineEntity();
        entity.setId(domain.getId());
        entity.setAthleteId(domain.getAthleteId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setRecommendedDurationMinutes(domain.getRecommendedDurationMinutes());
        entity.setRecommendedIntensity(mapRoutineIntensityToEnum(domain.getRecommendedIntensity()));
        entity.setRecoverySuggestion(mapRecoverySuggestionToEnum(domain.getRecoverySuggestion()));
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private SportType mapSportType(AthleteEntity.SportTypeEnum enumValue) {
        if (enumValue == null) return null;
        return SportType.valueOf(enumValue.name());
    }

    private AthleteEntity.SportTypeEnum mapSportTypeToEnum(SportType sportType) {
        if (sportType == null) return null;
        return AthleteEntity.SportTypeEnum.valueOf(sportType.name());
    }

    private Intensity mapIntensity(TrainingSessionEntity.IntensityEnum enumValue) {
        if (enumValue == null) return null;
        return Intensity.valueOf(enumValue.name());
    }

    private TrainingSessionEntity.IntensityEnum mapIntensityToEnum(Intensity intensity) {
        if (intensity == null) return null;
        return TrainingSessionEntity.IntensityEnum.valueOf(intensity.name());
    }

    private Intensity mapRoutineIntensity(RoutineEntity.IntensityEnum enumValue) {
        if (enumValue == null) return null;
        return Intensity.valueOf(enumValue.name());
    }

    private RoutineEntity.IntensityEnum mapRoutineIntensityToEnum(Intensity intensity) {
        if (intensity == null) return null;
        return RoutineEntity.IntensityEnum.valueOf(intensity.name());
    }

    private RecoverySuggestion mapRecoverySuggestion(RoutineEntity.RecoverySuggestionEnum enumValue) {
        if (enumValue == null) return null;
        return RecoverySuggestion.valueOf(enumValue.name());
    }

    private RoutineEntity.RecoverySuggestionEnum mapRecoverySuggestionToEnum(RecoverySuggestion suggestion) {
        if (suggestion == null) return null;
        return RoutineEntity.RecoverySuggestionEnum.valueOf(suggestion.name());
    }
}