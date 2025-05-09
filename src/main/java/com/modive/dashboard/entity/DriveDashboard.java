package com.modive.dashboard.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.modive.dashboard.dto.DriveFeedbacksDto;
import com.modive.dashboard.dto.ScoreDto;
import com.modive.dashboard.tools.TypeConverter;
import lombok.Data;

import java.time.Instant;

@DynamoDBTable(tableName = "drive-dashboard")
@Data
public class DriveDashboard {
    // Partition Key
    private String userId;
    // Sort Key
    private String driveId;


    @DynamoDBTypeConverted(converter = TypeConverter.InstantConverter.class)
    private Instant startTime;

    @DynamoDBTypeConverted(converter = TypeConverter.InstantConverter.class)
    private Instant endTime;

    private ScoreDto scores;

    private DriveFeedbacksDto feedbacks;

    // getter
    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDBRangeKey(attributeName = "driveId")
    public String getDriveId() {
        return driveId;
    }

}