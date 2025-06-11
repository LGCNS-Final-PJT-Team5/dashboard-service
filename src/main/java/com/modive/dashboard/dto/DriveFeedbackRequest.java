package com.modive.dashboard.dto;

import lombok.Data;

@Data
public class DriveFeedbackRequest {
    private int rapidAccelerationDecelerationCount;
    private int sharpTurnCount;
    private int overspeedCount;

    private int idlingTimeMinutes;

    private int steadySpeedLowRatio;
    private int steadySpeedMiddleRatio;
    private int steadySpeedHighRatio;

    private double reactionDelayCount; //averageReactionTimeSeconds

    private int laneDepartureCount;
    private int safeDistanceNotMaintainCount; //safeDistanceNotMaintainSeconds

    private int totalDrivingMinutes;
    private int inactivityCount;
}
