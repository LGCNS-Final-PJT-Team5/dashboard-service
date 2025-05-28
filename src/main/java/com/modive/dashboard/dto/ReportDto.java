package com.modive.dashboard.dto;

import com.modive.dashboard.entity.TotalDashboard;
import com.modive.dashboard.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
public class ReportDto {
    public String userId;                   // 사용자 ID
    public UserType userType;

    public int driveCount;                  // 누적운전횟수
    public ScoreDto scores;

    public TotalFeedback totalFeedback;
    public DetailedFeedback detailedFeedback;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TotalFeedback {
        public String title;
        public String content;
    }

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
}
