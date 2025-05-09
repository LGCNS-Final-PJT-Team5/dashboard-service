package com.modive.dashboard.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Data;

@Data
@DynamoDBDocument
public class ScoreDto {
    public double idlingScore;
    public double speedMaintainScore;
    public double ecoScore;

    public double accelerationScore;
    public double sharpTurnScore;
    public double overSpeedScore;
    public double safetyScore;

    public double reactionScore;
    public double laneDepartureScore;
    public double followingDistanceScore;
    public double accidentPreventionScore;

    public double drivingTimeScore;
    public double inactivityScore;
    public double attentionScore;

    public double totalScore;

}
