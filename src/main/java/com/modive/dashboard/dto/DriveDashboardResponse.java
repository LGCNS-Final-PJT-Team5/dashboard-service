package com.modive.dashboard.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.modive.dashboard.entity.DriveDashboard;
import com.modive.dashboard.tools.TypeConverter;
import lombok.Getter;

import java.time.Instant;

@Getter
public class DriveDashboardResponse {
    private String userId;
    private String driveId;
    private Instant startTime;
    private Instant endTime;
    private ScoreDto scores;
    private String feedback;

    public DriveDashboardResponse(DriveDashboard dashboard) {
        this.userId = dashboard.getUserId();
        this.driveId = dashboard.getDriveId();

        this.startTime = dashboard.getStartTime();
        this.endTime = dashboard.getEndTime();
        this.scores = dashboard.getScores();

        if (dashboard.getFeedbacks() != null) {
            this.feedback = dashboard.getFeedbacks().totalFeedback;
        }
    }
}
