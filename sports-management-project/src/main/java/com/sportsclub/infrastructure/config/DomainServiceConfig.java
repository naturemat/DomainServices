package com.sportsclub.infrastructure.config;

import com.sportsclub.training.domain.policy.FatigueConfiguration;
import com.sportsclub.training.domain.service.FatigueCalculator;
import com.sportsclub.training.domain.service.RecoverySuggester;
import com.sportsclub.training.domain.service.RoutineRecommender;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.training.domain.port.out.TrainingSessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {
    @Bean public FatigueConfiguration fatigueRules() { return new FatigueConfiguration(); }
    @Bean public FatigueCalculator fatigueCalculator(FatigueConfiguration fatigueRules) { return new FatigueCalculator(fatigueRules); }
    @Bean public RoutineRecommender routineRecommender(AthleteRepository athleteRepository, FatigueCalculator fatigueCalculator, RecoverySuggester recoverySuggester, TrainingSessionRepository sessionRepository, FatigueConfiguration fatigueRules) { return new RoutineRecommender(athleteRepository, fatigueCalculator, recoverySuggester, sessionRepository, fatigueRules); }
    @Bean public RecoverySuggester recoverySuggester(FatigueConfiguration fatigueRules) { return new RecoverySuggester(fatigueRules); }
}