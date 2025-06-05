package com.modive.dashboard.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.modive.dashboard.dto.ScoreDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@DynamoDBTable(tableName = "statistics")
@Data
@NoArgsConstructor
public class Statistics {
    @DynamoDBHashKey
    private String statId; // total / 202504 ...

    private ScoreDto averageScore;

    private int totalDriveCount = 0;
    private Long totalDriveMinutes = (long) 0;

    // getter
    @DynamoDBHashKey(attributeName = "statId")
    public String getStatId() {
        return statId;
    }


    public Statistics(String statId){
        this.statId = statId;
        averageScore = new ScoreDto();
    }
}