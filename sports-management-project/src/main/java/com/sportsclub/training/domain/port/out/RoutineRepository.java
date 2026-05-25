package com.sportsclub.training.domain.port.out;

import com.sportsclub.training.domain.model.entity.Routine;
import java.util.List;
import java.util.UUID;

public interface RoutineRepository {
    Routine save(Routine routine);

    List<Routine> findByAthleteId(UUID athleteId);

    Routine findLatestByAthleteId(UUID athleteId);
}