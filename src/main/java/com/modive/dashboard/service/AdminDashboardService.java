package com.modive.dashboard.service;

import com.modive.dashboard.dto.admin.AdminResponse;
import com.modive.dashboard.dto.admin.MonthlyDrivesStatistics;
import com.modive.dashboard.dto.admin.TotalDriveCount;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminDashboardService {

    // 1. 총 주행 수
    TotalDriveCount getDriveCount();

    // 2. 월별 운전 횟수 조회 (그래프)
    List<MonthlyDrivesStatistics> getDriveCountByMonth();
//
//    // 3. 사용자별 운전 횟수 (사용자 정보)
//    ResponseEntity<AdminResponse> getDriveCountByUser();
//
//    // 4. 특정 사용자 운전 내역 (사용자 상세 조회)
//    ResponseEntity<AdminResponse> getDrivesByUser();

}
