package com.modive.dashboard.service;

import com.modive.dashboard.client.LLMClient;
import com.modive.dashboard.dto.*;
import com.modive.dashboard.dto.admin.AdminResponse;
import com.modive.dashboard.dto.admin.TotalDriveCount;
import com.modive.dashboard.entity.Drive;
import com.modive.dashboard.entity.DriveDashboard;
import com.modive.dashboard.entity.Statistics;
import com.modive.dashboard.enums.ScoreType;
import com.modive.dashboard.repository.DriveDashboardRepository;
import com.modive.dashboard.repository.DriveRepository;
import com.modive.dashboard.repository.StatisticsRepository;
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

}
