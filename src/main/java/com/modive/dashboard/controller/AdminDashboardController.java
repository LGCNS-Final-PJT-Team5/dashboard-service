package com.modive.dashboard.controller;

import com.modive.dashboard.dto.admin.AdminResponse;
import com.modive.dashboard.dto.admin.TotalDriveCount;
import com.modive.dashboard.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
                    new AdminResponse<>(200, "발급 사유별 월별 통계에 성공했습니다.",  Map.of("totalDriveCount", totalDriveCount));
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

    // 3. 사용자별 운전 횟수 (사용자 정보)

    // 4. 특정 사용자 운전 내역 (사용자 상세 조회)
}
