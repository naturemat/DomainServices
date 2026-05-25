package com.sportsclub.infrastructure.adapter.out.persistence.postgres.repository;

import com.sportsclub.training.domain.model.entity.AthleteProfile;
import com.sportsclub.training.domain.port.out.AthleteProfileRepository;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public class PostgresAthleteProfileRepository implements AthleteProfileRepository {
    private final AthleteRepository athleteRepository;

    public PostgresAthleteProfileRepository(AthleteRepository athleteRepository) {
        this.athleteRepository = athleteRepository;
    }

    @Override
    public AthleteProfile findByAthleteId(UUID athleteId) {
        return athleteRepository.findById(athleteId)
                .map(athlete -> new AthleteProfile(UUID.randomUUID(), athlete))
                .orElse(null);
    }
}
