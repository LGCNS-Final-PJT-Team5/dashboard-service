package com.modive.dashboard.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Data;

@Data
@DynamoDBDocument
public class DriveFeedbacksDto {
    public String idlingFeedback;
    public String speedMaintainFeedback;
    public String ecoFeedback;

    public String accelerationFeedback;
    public String sharpTurnFeedback;
    public String overSpeedFeedback;
    public String safetyFeedback;

    public String reactionFeedback;
    public String laneDepartureFeedback;
    public String followingDistanceFeedback;
    public String accidentPreventionFeedback;

    public String drivingTimeFeedback;
    public String inactivityFeedback;
    public String attentionFeedback;

    public String totalFeedback;
}
