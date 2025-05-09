package com.modive.dashboard.service;

import com.modive.dashboard.dto.TotalDashboardResponse;
import com.modive.dashboard.enums.UserType;
import org.springframework.stereotype.Service;

@Service
public class TotalDashboardServiceImpl implements TotalDashboardService {

    // 1. 누적 대시보드 생성
    @Override
    public void createTotalDashboard(String userId) {

    }

    // 2. 누적 대시보드 조회
    @Override
    public TotalDashboardResponse getTotalDashboard(String userId) {
        return null;
    }

    // 3. 주간 맞춤형 리포트 생성 및 조회
    @Override
    public Object makeReport(String userId, UserType userType) {
        return null;
    }

    // 4. 누적 대시보드 업데이트
    @Override
    public void updateTotalDashboard(Long userId) {

    }
}
