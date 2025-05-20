package com.modive.dashboard.service;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.modive.dashboard.dto.PaginatedListResponse;
import com.modive.dashboard.dto.admin.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface AdminDashboardService {

    // 1. 총 주행 수
    TotalDriveCount getDriveCount();

    // 2. 월별 운전 횟수 조회 (그래프)
    List<MonthlyDrivesStatistics> getDriveCountByMonth();

    // 3. 사용자별 운전 횟수 (사용자 정보)
    List<DriveCountByUser> getDriveCountByUser(UserIdListRequest userIds);

    // 4. 특정 사용자 운전 내역 (사용자 상세 조회)
    PaginatedListResponse<DriveHistory> getDrivesByUser(String userId, String startTime, String driveId, int pageSize);

}
