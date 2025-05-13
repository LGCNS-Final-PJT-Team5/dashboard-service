package com.modive.dashboard.service;

import com.modive.dashboard.dto.ScoreDto;
import com.modive.dashboard.dto.TotalDashboardResponse;
import com.modive.dashboard.entity.DriveDashboard;
import com.modive.dashboard.entity.TotalDashboard;
import com.modive.dashboard.enums.UserType;
import com.modive.dashboard.repository.TotalDashboardRepository;
import com.modive.dashboard.tools.ScoreCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TotalDashboardServiceImpl implements TotalDashboardService {
    @Autowired
    private TotalDashboardRepository totalDashboardRepository;
    @Autowired
    private ScoreCalculator scoreCalculator;

    // 1. 누적 대시보드 생성
    @Override
    public void createTotalDashboard(String userId) {
        TotalDashboard dashboard = new TotalDashboard();

        dashboard.setUserId(userId);
        dashboard.setDashboardId(UUID.randomUUID().toString());
        dashboard.setScores(new ScoreDto());
        dashboard.setTotalDriveCount(0);
        dashboard.setCreatedAt(Instant.now());

        totalDashboardRepository.save(dashboard);
    }

    // 2. 누적 대시보드 조회
    @Override
    public TotalDashboardResponse getTotalDashboard(String userId) {

        TotalDashboard dashboard = totalDashboardRepository.findById(userId);
        TotalDashboardResponse result = new TotalDashboardResponse(dashboard);

        return result;
    }

    // 3. 주간 맞춤형 리포트 생성 및 조회
    @Override
    public Object makeReport(String userId, UserType userType) {
        return null;
    }

    // 4. 누적 대시보드 업데이트
    @Override
    public void updateTotalDashboard(String userId, DriveDashboard driveDashboard) {
        TotalDashboard totalDashboard = totalDashboardRepository.findById(userId);

        totalDashboard.setUpdatedAt(driveDashboard.getEndTime());
        totalDashboard.setTotalDriveCount(totalDashboard.getTotalDriveCount() + 1);
        totalDashboard.setScores(scoreCalculator.calculateTotalScore(totalDashboard.getScores(), driveDashboard.getScores(), totalDashboard.getTotalDriveCount()));

        totalDashboardRepository.save(totalDashboard);
    }
}
