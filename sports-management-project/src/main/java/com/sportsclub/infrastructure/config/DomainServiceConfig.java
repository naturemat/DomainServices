package com.sportsclub.infrastructure.config;

import com.sportsclub.training.domain.policy.FatigueRules;
import com.sportsclub.training.domain.service.FatigueCalculator;
import com.sportsclub.training.domain.service.RecoverySuggester;
import com.sportsclub.training.domain.service.RoutineRecommender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {
    @Bean public FatigueRules fatigueRules() { return new FatigueRules(); }
    @Bean public FatigueCalculator fatigueCalculator(FatigueRules fatigueRules) { return new FatigueCalculator(fatigueRules); }
    @Bean public RoutineRecommender routineRecommender() { return new RoutineRecommender(); }
    @Bean public RecoverySuggester recoverySuggester() { return new RecoverySuggester(); }
}