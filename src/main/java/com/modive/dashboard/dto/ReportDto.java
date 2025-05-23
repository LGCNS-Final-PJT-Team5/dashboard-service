package com.modive.dashboard.dto;

import com.modive.dashboard.entity.TotalDashboard;
import com.modive.dashboard.enums.UserType;
import lombok.Data;

import java.time.Instant;

@Data
public class ReportDto {
    public String userId;                   // 사용자 ID
    public UserType userType;

    public int driveCount;                  // 누적운전횟수
    public ScoreDto scores;

    public String totalFeedback;
    public String detailedFeedback;
    // 피드백 형식은 추후 구체화
}
