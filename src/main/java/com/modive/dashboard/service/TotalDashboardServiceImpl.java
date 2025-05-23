package com.modive.dashboard.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.modive.dashboard.dto.ReportDto;
import com.modive.dashboard.dto.ScoreDto;
import com.modive.dashboard.dto.TotalDashboardResponse;
import com.modive.dashboard.entity.Drive;
import com.modive.dashboard.entity.DriveDashboard;
import com.modive.dashboard.entity.Statistics;
import com.modive.dashboard.entity.TotalDashboard;
import com.modive.dashboard.enums.UserType;
import com.modive.dashboard.repository.DriveDashboardRepository;
import com.modive.dashboard.repository.StatisticsRepository;
import com.modive.dashboard.repository.TotalDashboardRepository;
import com.modive.dashboard.tools.NotFoundException;
import com.modive.dashboard.tools.ScoreCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TotalDashboardServiceImpl implements TotalDashboardService {
    @Autowired
    private TotalDashboardRepository totalDashboardRepository;
    @Autowired
    private ScoreCalculator scoreCalculator;
    @Autowired
    private StatisticsRepository statisticsRepository;
    @Autowired
    private DriveDashboardRepository driveDashboardRepository;

    // 1. 누적 대시보드 생성
    @Override
    public void createTotalDashboard(String userId) {
        TotalDashboard existing = totalDashboardRepository.findById(userId);

        if (existing != null) {
            throw new NotFoundException("[" + userId + "]에 해당하는 누적 대시보드가 이미 있습니다.");
        }

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

        if (dashboard == null) {
            throw new NotFoundException("[" + userId + "]에 해당하는 누적 대시보드가 없습니다.");
        }

        TotalDashboardResponse result = new TotalDashboardResponse(dashboard);

        return result;
    }

    // 3. 주간 맞춤형 리포트 생성 및 조회
    @Override
    public ReportDto makeReport(String userId) {

        ReportDto report = new ReportDto();

        List<DriveDashboard> dashboards = driveDashboardRepository.findByStartTimeAfter(userId, Instant.now().minus(7, ChronoUnit.DAYS));

        if (dashboards.isEmpty()) {
            throw new NotFoundException("[" + userId + "]에 해당하는 최근 일주일 주행 기록이 없습니다.");
        }

        List<ScoreDto> scores = dashboards.stream()
                .map(DriveDashboard::getScores)
                .toList();

        report.setUserId(userId);
        report.setUserType(UserType.ECO); // TODO: 유저 타입 받아오기
        report.setDriveCount(dashboards.size());
        report.setScores(scoreCalculator.calculateAverageScore(scores));

        report.setTotalFeedback("total feedback입니다.");
        report.setDetailedFeedback("detail feedback입니다.");

        return report;
    }

    //<editor-folder desc="# Async methods">
    // 4. 누적 대시보드 업데이트
    @Override
    public void updateTotalDashboard(String userId, DriveDashboard driveDashboard) {
        TotalDashboard totalDashboard = totalDashboardRepository.findById(userId);

        if (totalDashboard == null) {
            throw new NotFoundException("[" + userId + "]에 해당하는 누적 대시보드가 없습니다.");
        }

        totalDashboard.setUpdatedAt(driveDashboard.getEndTime());
        totalDashboard.setTotalDriveCount(totalDashboard.getTotalDriveCount() + 1);
        totalDashboard.setScores(scoreCalculator.calculateTotalScore(totalDashboard.getScores(), driveDashboard.getScores(), totalDashboard.getTotalDriveCount()));

        totalDashboardRepository.save(totalDashboard);
    }

    // 5. 평균 업데이트
    public void updateStatistics(Drive drive, ScoreDto score) {
        // Total Statistics
        Statistics totalStatistics = statisticsRepository.find("total");

        if (totalStatistics == null) {
            totalStatistics = new Statistics("total");
        }

        totalStatistics.setTotalDriveCount(totalStatistics.getTotalDriveCount() + 1);
        totalStatistics.setTotalDriveMinutes(totalStatistics.getTotalDriveMinutes() + Duration.between(drive.getStartTime(), drive.getEndTime()).toMinutes());
        totalStatistics.setAverageScore(scoreCalculator.calculateTotalScore(totalStatistics.getAverageScore(), score, totalStatistics.getTotalDriveCount()));

        statisticsRepository.save(totalStatistics);

        System.out.println(totalStatistics); // 임시

        // Monthly Statistics
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        LocalDate today = LocalDate.now();
        String month = today.format(formatter);
        Statistics monthStatistics = statisticsRepository.find(month);

        if (monthStatistics == null) {
            monthStatistics = new Statistics(month);
        }

        monthStatistics.setTotalDriveCount(monthStatistics.getTotalDriveCount() + 1);
        monthStatistics.setTotalDriveMinutes(monthStatistics.getTotalDriveMinutes() + Duration.between(drive.getStartTime(), drive.getEndTime()).toMinutes());
        monthStatistics.setAverageScore(scoreCalculator.calculateTotalScore(monthStatistics.getAverageScore(), score, monthStatistics.getTotalDriveCount()));

        statisticsRepository.save(monthStatistics);

        System.out.println(monthStatistics); // 임시

    }
    //</editor-folder>
}

