package com.modive.dashboard.controller;

import com.modive.dashboard.dto.DriveDashboardResponse;
import com.modive.dashboard.dto.PaginatedListResponse;
import com.modive.dashboard.dto.detail.DriveDetailDto;
import com.modive.dashboard.dto.DriveListDto;
import com.modive.dashboard.entity.Drive;
import com.modive.dashboard.enums.ScoreType;
import com.modive.dashboard.service.PostDriveDashboardService;
import com.modive.dashboard.tools.NotFoundException;
import com.modive.dashboard.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard/post-drive")
@RequiredArgsConstructor
public class PostDriveDashboardController {

    private final JwtUtils jwtUtils;

    @Autowired
    private PostDriveDashboardService postDriveDashboardService;

    // 1. 주행 후 대시보드 생성 및 누적 대시보드 업데이트 (주행 완료 처리)
    @PostMapping
    public ResponseEntity<Void> createPostDriveDashboard(
            @RequestParam String userId,
            @RequestParam String driveId
    ) {

        System.out.println("대시보드 생성 API 호출됨.");
        postDriveDashboardService.createPostDriveDashboard(userId, driveId);

        return ResponseEntity.noContent().build();
    }

    // 2. 주행 후 대시보드 조회
    @GetMapping("/{driveId}")
    public ResponseEntity<Object> getPostDriveDashboard(
            //@RequestHeader("X-User-Id") String userId, // TODO: userId 연동
            @PathVariable String driveId
    ) {
        String userId = jwtUtils.getUserId();
        DriveDashboardResponse drive = postDriveDashboardService.getPostDriveDashboard(userId, driveId);

        return drive == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(drive);
    }

    // 3. 주행 후 대시보드 상세 조회 (safe, eco, prevention, attention)
    @GetMapping("/{driveId}/{scoreType}")
    public ResponseEntity<Object> getDetailDashboard(
            //@RequestHeader("X-User-Id") String userId, // TODO: userId 연동
            @PathVariable String driveId,
            @PathVariable String scoreType
    ) {
        String userId = jwtUtils.getUserId();
        ScoreType type;
        try {
            type = ScoreType.fromString(scoreType);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("[" + scoreType + "]에 해당하는 타입이 없습니다.");
        }

        DriveDetailDto detail = postDriveDashboardService.getPostDriveDashboardByType(userId, driveId, type);

        return detail == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(detail);
    }

    // 4. 주행 후 대시보드 목록 조회
    @GetMapping()
    public ResponseEntity<Object> getPostDriveDashboards(
            //@RequestHeader("X-User-Id") String userId, // TODO: userId 연동
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String driveId
    ) {
        String userId = jwtUtils.getUserId();
        PaginatedListResponse<DriveListDto> dtos = postDriveDashboardService.getPostDriveDashboardList(userId, startTime, driveId, pageSize);
        return ResponseEntity.ok(dtos);
    }



}
