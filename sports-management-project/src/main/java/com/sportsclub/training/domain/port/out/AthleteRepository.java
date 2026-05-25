package com.sportsclub.training.domain.port.out;

import com.sportsclub.training.domain.model.entity.Athlete;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AthleteRepository {
    Optional<Athlete> findById(UUID id);

    List<Athlete> findAll();

    List<Athlete> findByNameContaining(String name);

    List<Athlete> findTop100ByOrderByName();

    Athlete save(Athlete athlete);

    boolean existsById(UUID id);
}