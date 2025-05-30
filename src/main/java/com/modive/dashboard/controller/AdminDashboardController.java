package com.modive.dashboard.controller;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.modive.dashboard.dto.PaginatedListResponse;
import com.modive.dashboard.dto.admin.*;
import com.modive.dashboard.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard/drives")
public class AdminDashboardController {
    @Autowired
    private AdminDashboardService adminDashboardService;

    // 1. 총 주행 수
    @GetMapping("/total")
    public ResponseEntity<AdminResponse<TotalDriveCount>> getTotalDashboard() {
        TotalDriveCount totalDriveCount = adminDashboardService.getDriveCount();

        if (totalDriveCount != null) {
            AdminResponse<TotalDriveCount> adminResponse =
                    new AdminResponse<>(200, "발급 사유별 월별 통계에 성공했습니다.",  Map.of("totalDrives", totalDriveCount));
            return ResponseEntity.ok(adminResponse); // 200 OK
        } else {
            AdminResponse<TotalDriveCount> adminResponse =
                    new AdminResponse<>(500, "발급 사유별 월별 통계에 실패했습니다.", null);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(adminResponse);
        }
    }


    // 2. 월별 운전 횟수 조회 (그래프)
    @GetMapping("/monthly-stats")
    public ResponseEntity<AdminResponse<List<MonthlyDrivesStatistics>>> getMonthlyStats() {

        List<MonthlyDrivesStatistics> list = adminDashboardService.getDriveCountByMonth();
        AdminResponse<List<MonthlyDrivesStatistics>> adminResponse = new AdminResponse<>(200, "월별 운전 횟수 조회에 성공하였습니다.",  Map.of("monthlyDrivesStatistics", list));
        return ResponseEntity.ok(adminResponse);
    }

    // 3. 사용자별 운전 횟수 (사용자 정보)
    @PostMapping("/by-user")
    public ResponseEntity<AdminResponse<List<DriveCountByUser>>> getStatsByUser(
            @RequestBody UserIdListRequest userIds
    ) {

        List<DriveCountByUser> list = adminDashboardService.getDriveCountByUser(userIds);
        AdminResponse<List<DriveCountByUser>> adminResponse = new AdminResponse<>(200, "사용자 운전 횟수 조회에 성공하였습니다.",  Map.of("driveCountByUser", list));
        return ResponseEntity.ok(adminResponse);
    }

    // 4. 특정 사용자 운전 내역 (사용자 상세 조회)
    @GetMapping("/{userId}")
    public ResponseEntity<AdminResponse<PaginatedListResponse<DriveHistory>>> getDriveCountByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String driveId
    ) {

        PaginatedListResponse<DriveHistory> list = adminDashboardService.getDrivesByUser(userId, startTime, driveId, pageSize);
        AdminResponse<PaginatedListResponse<DriveHistory>> adminResponse = new AdminResponse<>(200, "발급 사유별 월별 통계에 성공했습니다.",  Map.of("driveHistory", list));
        return ResponseEntity.ok(adminResponse);
    }

}
