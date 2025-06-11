package com.modive.dashboard.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Data;

@Data
@DynamoDBDocument
public class DriveFeedbacksDto {
    public String idlingTimeMinutesFeedback;
    public String steadySpeedRatioFeedback;
//    public String ecoFeedback;

    public String rapidAccelerationDecelerationCountFeedback;
    public String sharpTurnCountFeedback;
    public String overspeedCountFeedback;
//    public String safetyFeedback;

    public String reactionDelayCountFeedback;
    public String laneDepartureCountFeedback;
    public String safeDistanceNotMaintainCountFeedback;
//    public String accidentPreventionFeedback;

    public String totalDrivingMinutesFeedback;
    public String inactivityTimeMinutesFeedback;
//    public String attentionFeedback;

    public String totalFeedback;
}
