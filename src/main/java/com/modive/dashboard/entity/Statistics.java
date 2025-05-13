package com.modive.dashboard.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.modive.dashboard.dto.ScoreDto;
import lombok.Data;

@DynamoDBTable(tableName = "statistics")
@Data
public class Statistics {
    @DynamoDBHashKey
    private String statId = "statistics";

    private ScoreDto averageScore;

    private int totalDriveCount = 0;
    private Long totalDriveMinutes = (long) 0;

    // getter
    @DynamoDBHashKey(attributeName = "statId")
    public String getStatId() {
        return statId;
    }

}