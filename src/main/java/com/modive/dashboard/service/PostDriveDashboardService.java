package com.modive.dashboard.service;

import com.modive.dashboard.dto.DriveDashboardResponse;
import com.modive.dashboard.dto.PaginatedListResponse;
import com.modive.dashboard.dto.detail.DriveDetailDto;
import com.modive.dashboard.dto.DriveListDto;
import com.modive.dashboard.entity.Drive;
import com.modive.dashboard.enums.ScoreType;

import java.util.List;

public interface PostDriveDashboardService {

    // 1. 주행 후 대시보드 생성
    void createPostDriveDashboard(String userId, String driveId);

    // 2. 주행 후 대시보드 조회
    DriveDashboardResponse getPostDriveDashboard(String userId, String driveId);

    // 3. 주행 후 대시보드 상세 조회 (safe, eco, prevention, attention)
    DriveDetailDto getPostDriveDashboardByType(String userId, String driveId, ScoreType type);

    // 4. 주행 후 대시보드 목록 조회
    PaginatedListResponse<DriveListDto> getPostDriveDashboardList(String userId, String startTime, String driveId, int pageSize);
}
