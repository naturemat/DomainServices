package infrastructure.config;

import domain.policies.FatigueRules;
import domain.services.FatigueCalculationService;
import domain.services.RecoverySuggestionService;
import domain.services.RoutineRecommendationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public FatigueRules fatigueRules() {
        return new FatigueRules();
    }

    @Bean
    public FatigueCalculationService fatigueCalculationService(FatigueRules fatigueRules) {
        return new FatigueCalculationService(fatigueRules);
    }

    @Bean
    public RoutineRecommendationService routineRecommendationService() {
        return new RoutineRecommendationService();
    }

    @Bean
    public RecoverySuggestionService recoverySuggestionService() {
        return new RecoverySuggestionService();
    }
}