package com.sportsclub.training.domain.policy;

import com.sportsclub.shared.domain.model.FatigueLevel;

public class FatigueConfiguration {
    private final int recoveryWindowHours;
    private final int highFatigueThreshold;
    private final int mediumFatigueThreshold;
    private final int restDayReductionRate;

    public FatigueConfiguration() {
        this(72, 30, 15, 1);
    }

    public FatigueConfiguration(int recoveryWindowHours, int highFatigueThreshold, int mediumFatigueThreshold,
            int restDayReductionRate) {
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

    public boolean isHighFatigue(int fatiguePoints) {
        return fatiguePoints >= highFatigueThreshold;
    }

    public boolean isMediumFatigue(int fatiguePoints) {
        return fatiguePoints >= mediumFatigueThreshold && !isHighFatigue(fatiguePoints);
    }

    public boolean isLowFatigue(int fatiguePoints) {
        return fatiguePoints < mediumFatigueThreshold;
    }

    public FatigueLevel classifyFatigue(int fatiguePoints) {
        if (isHighFatigue(fatiguePoints))
            return FatigueLevel.HIGH;
        if (isMediumFatigue(fatiguePoints))
            return FatigueLevel.MEDIUM;
        return FatigueLevel.LOW;
    }

    public int calculateRecoveryAfterRest(int currentFatigue, int restDays) {
        return Math.max(0, currentFatigue - (restDays * restDayReductionRate));
    }

    public boolean needsAbsoluteRest(int fatiguePoints, int sessionsThisWeek) {
        return isHighFatigue(fatiguePoints) && sessionsThisWeek >= 5;
    }
}
