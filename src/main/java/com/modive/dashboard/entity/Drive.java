package com.modive.dashboard.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.modive.dashboard.tools.TypeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@DynamoDBTable(tableName = "drive")
@Data
public class Drive {
    // Partition Key
    private String userId;
    // Sort Key
    private String driveId;


    @DynamoDBTypeConverted(converter = TypeConverter.InstantConverter.class)
    private Instant startTime;

    @DynamoDBTypeConverted(converter = TypeConverter.InstantConverter.class)
    private Instant endTime;

    private int activeDriveDurationSec;

    @DynamoDBTypeConverted(converter = TypeConverter.InstantListConverter.class)
    private List<Instant> suddenAccelerations;

    @DynamoDBTypeConverted(converter = TypeConverter.InstantListConverter.class)
    private List<Instant> sharpTurns;

    private List<SpeedLog> speedLogs;

    private List<StartEndTime> idlingPeriods;

    private List<SpeedRate> speedRate;

    private List<StartEndTime> reactionTimes;

    @DynamoDBTypeConverted(converter = TypeConverter.InstantListConverter.class)
    private List<Instant> laneDepartures;

    private List<StartEndTime> followingDistanceEvents;

    @DynamoDBTypeConverted(converter = TypeConverter.InstantListConverter.class)
    private List<Instant> inactiveMoments;

    // <editor-fold desc="# Getter for key">
    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDBRangeKey(attributeName = "driveId")
    public String getDriveId() {
        return driveId;
    }
    // </editor-fold>

    //<editor-fold desc="# Inner Classes">
    @DynamoDBDocument
    @Data
    public static class SpeedLog {
        private int period;
        private int maxSpeed;
    }

    @DynamoDBDocument
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StartEndTime {
        @DynamoDBTypeConverted(converter = TypeConverter.InstantConverter.class)
        private Instant startTime;

        @DynamoDBTypeConverted(converter = TypeConverter.InstantConverter.class)
        private Instant endTime;
    }

    @DynamoDBDocument
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SpeedRate {
        private String tag;
        private int ratio;
    }

    @DynamoDBDocument
    @Data
    public static class TimeWithFlag {
        @DynamoDBTypeConverted(converter = TypeConverter.InstantConverter.class)
        private Instant time;
        @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
        private boolean flag;
    }
    //</editor-fold>
}