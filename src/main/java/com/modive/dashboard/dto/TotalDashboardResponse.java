package com.modive.dashboard.dto;

import com.modive.dashboard.entity.TotalDashboard;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class TotalDashboardResponse {
    public String userId;                   // 사용자 ID
    public Instant lastDrive;         // 최근 운전일
    public int driveCount;                  // 누적운전횟수

    public ScoreDto scores;

    public TotalDashboardResponse(TotalDashboard dashboard)
    {
        this.userId = dashboard.getUserId();
        this.lastDrive = dashboard.getUpdatedAt();
        this.driveCount = dashboard.getTotalDriveCount();
        this.scores = dashboard.getScores();
    }
}
