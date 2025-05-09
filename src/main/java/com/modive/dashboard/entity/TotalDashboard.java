package com.modive.dashboard.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.modive.dashboard.dto.DriveFeedbacksDto;
import com.modive.dashboard.dto.ScoreDto;
import com.modive.dashboard.tools.TypeConverter;
import lombok.Data;

import java.time.Instant;

@DynamoDBTable(tableName = "total-dashboard")
@Data
public class TotalDashboard {
    // Partition Key
    private String userId;
    // Sort Key
    private String dashboardId;

    private ScoreDto scores;
    private int totalDriveCount;


    @DynamoDBTypeConverted(converter = TypeConverter.InstantConverter.class)
    private Instant createdAt;

    @DynamoDBTypeConverted(converter = TypeConverter.InstantConverter.class)
    private Instant updatedAt;

    // getter
    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDBRangeKey(attributeName = "dashboardId")
    public String getDashboardId() {
        return dashboardId;
    }

}