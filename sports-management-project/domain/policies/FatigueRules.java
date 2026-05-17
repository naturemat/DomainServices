package domain.policies;

import org.springframework.stereotype.Component;

@Component
public class FatigueRules {
    private final int recoveryWindowHours;
    private final int highFatigueThreshold;
    private final int mediumFatigueThreshold;
    private final int restDayReductionRate;

    public FatigueRules() {
        this(72, 30, 15, 1);
    }

    public FatigueRules(int recoveryWindowHours, int highFatigueThreshold,
                       int mediumFatigueThreshold, int restDayReductionRate) {
        this.recoveryWindowHours = recoveryWindowHours;
        this.highFatigueThreshold = highFatigueThreshold;
        this.mediumFatigueThreshold = mediumFatigueThreshold;
        this.restDayReductionRate = restDayReductionRate;
    }

    public int getRecoveryWindowHours() {
        return recoveryWindowHours;
    }

    public int getHighFatigueThreshold() {
        return highFatigueThreshold;
    }

    public int getMediumFatigueThreshold() {
        return mediumFatigueThreshold;
    }

    public int getRestDayReductionRate() {
        return restDayReductionRate;
    }
}