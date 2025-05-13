package com.modive.dashboard.service;

import com.modive.dashboard.dto.ScoreDto;
import com.modive.dashboard.dto.TotalDashboardResponse;
import com.modive.dashboard.entity.Drive;
import com.modive.dashboard.entity.DriveDashboard;
import com.modive.dashboard.entity.Statistics;
import com.modive.dashboard.entity.TotalDashboard;
import com.modive.dashboard.enums.UserType;
import com.modive.dashboard.repository.StatisticsRepository;
import com.modive.dashboard.repository.TotalDashboardRepository;
import com.modive.dashboard.tools.ScoreCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class TotalDashboardServiceImpl implements TotalDashboardService {
    @Autowired
    private TotalDashboardRepository totalDashboardRepository;
    @Autowired
    private ScoreCalculator scoreCalculator;
    @Autowired
    private StatisticsRepository statisticsRepository;

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

    //<editor-folder desc="# Async methods">
    // 4. 누적 대시보드 업데이트
    @Override
    public void updateTotalDashboard(String userId, DriveDashboard driveDashboard) {
        TotalDashboard totalDashboard = totalDashboardRepository.findById(userId);

        totalDashboard.setUpdatedAt(driveDashboard.getEndTime());
        totalDashboard.setTotalDriveCount(totalDashboard.getTotalDriveCount() + 1);
        totalDashboard.setScores(scoreCalculator.calculateTotalScore(totalDashboard.getScores(), driveDashboard.getScores(), totalDashboard.getTotalDriveCount()));

        totalDashboardRepository.save(totalDashboard);
    }

    // 5. 평균 업데이트
    public void updateStatistics(Drive drive, ScoreDto score) {
        Statistics statistics = statisticsRepository.find("statistics");

        if (statistics == null) {
            statistics = new Statistics();
            statistics.setAverageScore(new ScoreDto());
        }

        statistics.setTotalDriveCount(statistics.getTotalDriveCount() + 1);
        statistics.setTotalDriveMinutes(statistics.getTotalDriveMinutes() + Duration.between(drive.getStartTime(), drive.getEndTime()).toMinutes());
        statistics.setAverageScore(scoreCalculator.calculateTotalScore(statistics.getAverageScore(), score, statistics.getTotalDriveCount()));

        statisticsRepository.save(statistics);

        System.out.println(statistics); // 임시
    }
    //</editor-folder>
}

