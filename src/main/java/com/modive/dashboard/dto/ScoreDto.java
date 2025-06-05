package com.modive.dashboard.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DynamoDBDocument
public class ScoreDto {
    public double idlingScore = 0.0;
    public double speedMaintainScore = 0.0;
    public double ecoScore = 0.0;

    public double accelerationScore = 0.0;
    public double sharpTurnScore = 0.0;
    public double overSpeedScore = 0.0;
    public double safetyScore = 0.0;

    public double reactionScore = 0.0;
    public double laneDepartureScore = 0.0;
    public double followingDistanceScore = 0.0;
    public double accidentPreventionScore = 0.0;

    public double drivingTimeScore = 0.0;
    public double inactivityScore = 0.0;
    public double attentionScore = 0.0;

    public double totalScore = 0.0;

    public RewardDto.EarnComplexRequest.ScoreInfo toScoreInfo() {
        return RewardDto.EarnComplexRequest.ScoreInfo.builder()
                .carbon((int) ecoScore)
                .safety((int) safetyScore)
                .accident((int) accelerationScore)
                .focus((int) attentionScore)
                .build();
    }
}
