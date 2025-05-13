package com.modive.dashboard.service;

import com.modive.dashboard.dto.ScoreDto;
import com.modive.dashboard.dto.TotalDashboardResponse;
import com.modive.dashboard.entity.Drive;
import com.modive.dashboard.entity.DriveDashboard;
import com.modive.dashboard.enums.UserType;

public interface TotalDashboardService {

    // 1. 누적 대시보드 생성
    void createTotalDashboard(String userId);

    // 2. 누적 대시보드 조회
    TotalDashboardResponse getTotalDashboard(String userId);

    // 3. 주간 맞춤형 리포트 생성 및 조회
    Object makeReport(String userId, UserType userType);

    // 4. 누적 대시보드 업데이트
    void updateTotalDashboard(String userId, DriveDashboard dashboard);

    void updateStatistics(Drive drive, ScoreDto score);

}
