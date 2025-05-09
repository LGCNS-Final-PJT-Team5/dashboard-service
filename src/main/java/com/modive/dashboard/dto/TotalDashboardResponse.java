package com.modive.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TotalDashboardResponse {
    private String userId;                   // 사용자 ID
    private LocalDateTime lastDrive;         // 최근 운전일
    private int driveCount;                  // 누적운전횟수

    private ScoreDto scores;
}
