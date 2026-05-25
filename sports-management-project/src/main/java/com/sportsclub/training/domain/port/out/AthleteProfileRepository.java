package com.sportsclub.training.domain.port.out;

import com.sportsclub.training.domain.model.entity.AthleteProfile;
import java.util.UUID;

public interface AthleteProfileRepository {
    AthleteProfile findByAthleteId(UUID athleteId);
}
