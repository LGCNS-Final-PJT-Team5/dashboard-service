package com.modive.dashboard.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.modive.dashboard.dto.ReportDto;
import com.modive.dashboard.dto.ReportRequest;
import com.modive.dashboard.dto.ScoreDto;
import com.modive.dashboard.enums.UserType;
import com.modive.dashboard.tools.TypeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@DynamoDBTable(tableName = "weekly-dashboard")
@Data
public class WeeklyDashboard {

    private String dashboardId;
    private String userId;

    @DynamoDBTypeConvertedEnum
    private UserType userType;

    private int driveCount;
    private ScoreDto scores;

    private ReportDto.TotalFeedback totalFeedback;
    private ReportDto.DetailedFeedback detailedFeedback;

    private String createdAt;

    @DynamoDBHashKey(attributeName = "dashboardId")
    public String getDashboardId() {
        return dashboardId;
    }

    public ReportDto toReportDto() {
        ReportDto reportDto = new ReportDto();
        reportDto.setUserId(userId);
        reportDto.setUserType(userType);
        reportDto.setDriveCount(driveCount);
        reportDto.setScores(scores);
        reportDto.setTotalFeedback(totalFeedback);
        reportDto.setDetailedFeedback(detailedFeedback);
        return reportDto;
    }
}