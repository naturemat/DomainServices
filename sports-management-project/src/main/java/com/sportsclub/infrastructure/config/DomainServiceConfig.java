package com.sportsclub.infrastructure.config;

import com.sportsclub.training.domain.policy.FatigueConfiguration;
import com.sportsclub.training.domain.service.FatigueCalculator;
import com.sportsclub.training.domain.service.RecoverySuggester;
import com.sportsclub.training.domain.service.RoutineRecommender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {
    @Bean public FatigueConfiguration fatigueRules() { return new FatigueConfiguration(); }
    @Bean public FatigueCalculator fatigueCalculator(FatigueConfiguration fatigueRules) { return new FatigueCalculator(fatigueRules); }
    @Bean public RoutineRecommender routineRecommender(com.sportsclub.training.domain.port.out.AthleteProfileRepository profileRepository, FatigueCalculator fatigueCalculator, com.sportsclub.training.domain.port.out.TrainingSessionRepository sessionRepository) { return new RoutineRecommender(profileRepository, fatigueCalculator, sessionRepository); }
    @Bean public RecoverySuggester recoverySuggester() { return new RecoverySuggester(); }
}