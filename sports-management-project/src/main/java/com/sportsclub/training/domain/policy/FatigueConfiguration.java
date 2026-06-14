package com.sportsclub.training.domain.policy;

import com.sportsclub.training.domain.model.valueobject.FatigueLevel;

public class FatigueConfiguration {
    private final int recoveryWindowHours;
    private final int highFatigueThreshold;
    private final int mediumFatigueThreshold;
    private final int restDayReductionRate;
    private final int highVolumeThreshold;
    private final int youthMaxDuration;

    public FatigueConfiguration() {
        this(72, 30, 15, 1, 300, 40);
    }

    public FatigueConfiguration(int recoveryWindowHours, int highFatigueThreshold, int mediumFatigueThreshold,
            int restDayReductionRate) {
        this(recoveryWindowHours, highFatigueThreshold, mediumFatigueThreshold, restDayReductionRate, 300, 40);
    }

    public FatigueConfiguration(int recoveryWindowHours, int highFatigueThreshold, int mediumFatigueThreshold,
            int restDayReductionRate, int highVolumeThreshold, int youthMaxDuration) {
        this.recoveryWindowHours = recoveryWindowHours;
        this.highFatigueThreshold = highFatigueThreshold;
        this.mediumFatigueThreshold = mediumFatigueThreshold;
        this.restDayReductionRate = restDayReductionRate;
        this.highVolumeThreshold = highVolumeThreshold;
        this.youthMaxDuration = youthMaxDuration;
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

    public int getHighVolumeThreshold() {
        return highVolumeThreshold;
    }

    public int getYouthMaxDuration() {
        return youthMaxDuration;
    }

    public int adjustDurationForVolume(int baseDuration, int weeklyMinutes) {
        return weeklyMinutes > highVolumeThreshold ? (int) (baseDuration * 0.8) : baseDuration;
    }

    public int adjustDurationForAge(int duration, int age) {
        return age < 18 ? Math.min(duration, youthMaxDuration) : duration;
    }

    public FatigueLevel applyRestReduction(FatigueLevel currentFatigue, int restDays) {
        int reduced = currentFatigue.getValue() - (restDays * restDayReductionRate);
        if (reduced <= FatigueLevel.LOW.getValue()) return FatigueLevel.LOW;
        if (reduced <= FatigueLevel.MEDIUM.getValue()) return FatigueLevel.MEDIUM;
        return FatigueLevel.HIGH;
    }

    public FatigueLevel classifyFatigue(int fatiguePoints) {
        if (fatiguePoints >= highFatigueThreshold)
            return FatigueLevel.HIGH;
        if (fatiguePoints >= mediumFatigueThreshold)
            return FatigueLevel.MEDIUM;
        return FatigueLevel.LOW;
    }

    public int calculateRecoveryAfterRest(int currentFatigue, int restDays) {
        return Math.max(0, currentFatigue - (restDays * restDayReductionRate));
    }

    public boolean needsAbsoluteRest(int fatiguePoints, int sessionsThisWeek) {
        return fatiguePoints >= highFatigueThreshold && sessionsThisWeek >= 5;
    }
}
