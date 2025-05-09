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

    @Override
    public String toString() {
        return String.format(
                "Eco: %.2f (Idling: %.2f, SpeedMaintain: %.2f), Safety: %.2f (Accel: %.2f, Turn: %.2f, OverSpeed: %.2f), " +
                        "Prevention: %.2f (Reaction: %.2f, Lane: %.2f, Distance: %.2f), Attention: %.2f (Time: %.2f, Inactive: %.2f), Total: %.2f",
                ecoScore, idlingScore, speedMaintainScore,
                safetyScore, accelerationScore, sharpTurnScore, overSpeedScore,
                accidentPreventionScore, reactionScore, laneDepartureScore, followingDistanceScore,
                attentionScore, drivingTimeScore, inactivityScore,
                totalScore
        );
    }
}
