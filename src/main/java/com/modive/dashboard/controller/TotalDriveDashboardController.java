package com.modive.dashboard.controller;

import com.modive.dashboard.dto.ReportDto;
import com.modive.dashboard.dto.TotalDashboardResponse;
import com.modive.dashboard.service.TotalDashboardService;
import com.modive.dashboard.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/total")
public class TotalDriveDashboardController {

    private JwtUtils jwtUtils;

    @Autowired
    private TotalDashboardService totalDashboardService;

    // 1. 누적 대시보드 생성
    @PostMapping
    public ResponseEntity<Void> createTotalDashboard(
            //@RequestHeader("X-User-Id") String userId
    ) {
        String userId = jwtUtils.getUserId();
        // TODO: 예외 처리하기
        totalDashboardService.createTotalDashboard(userId);
        return ResponseEntity.noContent().build();
    }

    // 2. 누적 대시보드 조회
    @GetMapping
    public ResponseEntity<TotalDashboardResponse> getTotalDashboard(
            //@RequestHeader("X-User-Id") String userId
    ) {
        String userId = jwtUtils.getUserId();
        TotalDashboardResponse response = totalDashboardService.getTotalDashboard(userId);
        return response == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(response);
    }

    // 3. 주간 피드백 생성 및 조회
    @GetMapping("/report")
    public ResponseEntity<ReportDto> getTotalDashboardReport(
            //@RequestHeader("X-User-Id") String userId
    ) {
        String userId = jwtUtils.getUserId();
        ReportDto report = totalDashboardService.makeReport(userId);
        return report == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(report);
    }
}
