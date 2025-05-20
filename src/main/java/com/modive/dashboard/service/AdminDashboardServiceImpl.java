package com.modive.dashboard.service;

import com.modive.dashboard.client.LLMClient;
import com.modive.dashboard.dto.*;
import com.modive.dashboard.dto.admin.*;
import com.modive.dashboard.entity.Drive;
import com.modive.dashboard.entity.DriveDashboard;
import com.modive.dashboard.entity.Statistics;
import com.modive.dashboard.entity.TotalDashboard;
import com.modive.dashboard.enums.ScoreType;
import com.modive.dashboard.repository.DriveDashboardRepository;
import com.modive.dashboard.repository.DriveRepository;
import com.modive.dashboard.repository.StatisticsRepository;
import com.modive.dashboard.repository.TotalDashboardRepository;
import com.modive.dashboard.tools.LLMRequestGenerator;
import com.modive.dashboard.tools.ScoreCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {
    @Autowired
    private StatisticsRepository statisticsRepository;
    @Autowired
    private TotalDashboardRepository totalDashboardRepository;
    @Autowired
    private DriveDashboardRepository driveDashboardRepository;

    // 1. 총 주행 수
    @Override
    public TotalDriveCount getDriveCount() {
        int totalDriveCount = getDriveCountOrZero("total");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        LocalDate today = LocalDate.now();
        String lastMonth = today.minusMonths(1).format(formatter);
        String twoMonthsAgo = today.minusMonths(2).format(formatter);

        int lastMonthDriveCount = getDriveCountOrZero(lastMonth);
        int monthBeforeLastMonthDriveCount = getDriveCountOrZero(twoMonthsAgo);

        double changeRate = 0.0;
        if (monthBeforeLastMonthDriveCount != 0) {
            changeRate = 100.0 * ((double)(lastMonthDriveCount - monthBeforeLastMonthDriveCount) / monthBeforeLastMonthDriveCount);
        }

        return new TotalDriveCount(totalDriveCount, changeRate);
    }
    private int getDriveCountOrZero(String key) {
        Statistics stats = statisticsRepository.find(key);
        return (stats != null) ? stats.getTotalDriveCount() : 0;
    }

    // 2. 월별 운전 횟수 조회 (그래프)
    @Override
    public List<MonthlyDrivesStatistics> getDriveCountByMonth() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        LocalDate today = LocalDate.now();

        List<MonthlyDrivesStatistics> statisticsList = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            LocalDate targetMonth = today.minusMonths(i);
            String key = targetMonth.format(formatter);
            Statistics stats = statisticsRepository.find(key);

            int count = (stats != null) ? stats.getTotalDriveCount() : 0;

            statisticsList.add(new MonthlyDrivesStatistics(
                    targetMonth.getYear(),
                    targetMonth.getMonthValue(),
                    count
            ));
        }

        return statisticsList;
    }

    // 3. 사용자별 운전 횟수 (사용자 정보)
    @Override
    public List<DriveCountByUser> getDriveCountByUser(UserIdListRequest userIds) {

        List<DriveCountByUser> list = new ArrayList<>();

        for (String userId : userIds.getUserIds()) {
            TotalDashboard totalDashboard = totalDashboardRepository.findById(userId);

            int count = 0;
            if (totalDashboard != null) {
                count = totalDashboard.getTotalDriveCount();
            }

            list.add(new DriveCountByUser(
                    userId,
                    count
            ));
        }

        return list;
    }

    // 4. 특정 사용자 운전 내역 (사용자 상세 조회)
    @Override
    public List<DriveHistory> getDrivesByUser(String userId, int page, int pageSize) {

        List<DriveHistory> list = new ArrayList<>();
        List<DriveListDto> dtos = driveDashboardRepository.listByUserId(userId);

        for (DriveListDto dto : dtos) {
            list.add(dto.toDriveHistory());
        }

        return list;
    }

}
