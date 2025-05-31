package com.modive.dashboard.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.modive.dashboard.entity.TotalDashboard;
import com.modive.dashboard.entity.WeeklyDashboard;
import com.modive.dashboard.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class ReportDto {
    public String userId;                   // 사용자 ID
    public UserType userType;

    public int driveCount;                  // 누적운전횟수
    public ScoreDto scores;

    public TotalFeedback totalFeedback;
    public DetailedFeedback detailedFeedback;

    @DynamoDBDocument
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TotalFeedback {
        public String title;
        public String content;
    }

    @DynamoDBDocument
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailedFeedback {
        public String title;
        public String content;
        public List<String> feedback;
    }

    public ReportRequest ToReportRequest() {
        ReportRequest request = new ReportRequest();
        request.setUserId(userId);
        request.setUserType(userType);
        request.setScores(scores);

        return request;
    }

    public WeeklyDashboard ToWeeklyDashboard() {
        WeeklyDashboard dashboard = new WeeklyDashboard();
        dashboard.setUserId(userId);
        dashboard.setUserType(userType);
        dashboard.setScores(scores);
        dashboard.setDriveCount(driveCount);
        dashboard.setTotalFeedback(totalFeedback);
        dashboard.setDetailedFeedback(detailedFeedback);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
        String createdAt = formatter.format(Instant.now());
        dashboard.setCreatedAt(createdAt);

        dashboard.setDashboardId(userId + "-" + createdAt);

        return dashboard;
    }
}
