package com.sportsclub.infrastructure.adapter.out.persistence.postgres.mapper;

import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.model.enums.RecoverySuggestion;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.training.domain.model.valueobject.SessionId;
import com.sportsclub.infrastructure.adapter.out.persistence.postgres.entity.AthleteEntity;
import com.sportsclub.infrastructure.adapter.out.persistence.postgres.entity.RoutineEntity;
import com.sportsclub.infrastructure.adapter.out.persistence.postgres.entity.TrainingSessionEntity;
import java.util.List;
import java.util.stream.Collectors;

public class DomainEntityMapper {
    public Athlete toDomain(AthleteEntity entity) { if (entity == null) return null; return new Athlete(entity.getId(), entity.getName(), mapSportType(entity.getSportType()), entity.getBirthDate(), entity.getCreatedAt()); }
    public AthleteEntity toEntity(Athlete domain) { if (domain == null) return null; AthleteEntity entity = new AthleteEntity(); entity.setId(domain.getId()); entity.setName(domain.getName()); entity.setSportType(mapSportTypeToEnum(domain.getSportType())); entity.setBirthDate(domain.getBirthDate()); entity.setCreatedAt(domain.getCreatedAt()); return entity; }

    public TrainingSession toDomain(TrainingSessionEntity entity) { if (entity == null) return null; return new TrainingSession(SessionId.from(entity.getSessionId()), entity.getAthleteId(), entity.getSessionDate(), entity.getDurationMinutes(), mapIntensity(entity.getIntensity()), entity.getCaloriesBurned(), entity.getCreatedAt()); }
    public TrainingSessionEntity toEntity(TrainingSession domain) { if (domain == null) return null; TrainingSessionEntity entity = new TrainingSessionEntity(); entity.setSessionId(domain.getSessionId().getValue()); entity.setAthleteId(domain.getAthleteId()); entity.setSessionDate(domain.getSessionDate()); entity.setDurationMinutes(domain.getDurationMinutes()); entity.setIntensity(mapIntensityToEnum(domain.getIntensity())); entity.setCaloriesBurned(domain.getCaloriesBurned()); entity.setCreatedAt(domain.getCreatedAt()); return entity; }
    public List<TrainingSession> toDomainSessionList(List<TrainingSessionEntity> entities) { if (entities == null) return null; return entities.stream().map(this::toDomain).collect(Collectors.toList()); }

    public Routine toDomain(RoutineEntity entity) { if (entity == null) return null; return new Routine(entity.getId(), entity.getAthleteId(), entity.getName(), entity.getDescription(), entity.getRecommendedDurationMinutes(), mapRoutineIntensity(entity.getRecommendedIntensity()), mapRecoverySuggestion(entity.getRecoverySuggestion()), entity.getCreatedAt()); }
    public RoutineEntity toEntity(Routine domain) { if (domain == null) return null; RoutineEntity entity = new RoutineEntity(); entity.setId(domain.getId()); entity.setAthleteId(domain.getAthleteId()); entity.setName(domain.getName()); entity.setDescription(domain.getDescription()); entity.setRecommendedDurationMinutes(domain.getRecommendedDurationMinutes()); entity.setRecommendedIntensity(mapRoutineIntensityToEnum(domain.getRecommendedIntensity())); entity.setRecoverySuggestion(mapRecoverySuggestionToEnum(domain.getRecoverySuggestion())); entity.setCreatedAt(domain.getCreatedAt()); return entity; }

    private SportType mapSportType(AthleteEntity.SportTypeEnum e) { return e == null ? null : SportType.valueOf(e.name()); }
    private AthleteEntity.SportTypeEnum mapSportTypeToEnum(SportType s) { return s == null ? null : AthleteEntity.SportTypeEnum.valueOf(s.name()); }
    private Intensity mapIntensity(TrainingSessionEntity.IntensityEnum e) { return e == null ? null : Intensity.valueOf(e.name()); }
    private TrainingSessionEntity.IntensityEnum mapIntensityToEnum(Intensity i) { return i == null ? null : TrainingSessionEntity.IntensityEnum.valueOf(i.name()); }
    private Intensity mapRoutineIntensity(RoutineEntity.IntensityEnum e) { return e == null ? null : Intensity.valueOf(e.name()); }
    private RoutineEntity.IntensityEnum mapRoutineIntensityToEnum(Intensity i) { return i == null ? null : RoutineEntity.IntensityEnum.valueOf(i.name()); }
    private RecoverySuggestion mapRecoverySuggestion(RoutineEntity.RecoverySuggestionEnum e) { return e == null ? null : RecoverySuggestion.valueOf(e.name()); }
    private RoutineEntity.RecoverySuggestionEnum mapRecoverySuggestionToEnum(RecoverySuggestion s) { return s == null ? null : RoutineEntity.RecoverySuggestionEnum.valueOf(s.name()); }
}