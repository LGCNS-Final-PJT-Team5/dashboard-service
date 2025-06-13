package com.modive.dashboard.worker;

import com.modive.dashboard.client.LLMClient;
import com.modive.dashboard.client.RewardClient;
import com.modive.dashboard.dto.*;
import com.modive.dashboard.entity.Drive;
import com.modive.dashboard.entity.DriveDashboard;
import com.modive.dashboard.repository.DriveDashboardRepository;
import com.modive.dashboard.repository.DriveRepository;
import com.modive.dashboard.service.LLMService;
import com.modive.dashboard.service.TotalDashboardService;
import com.modive.dashboard.tools.LLMRequestGenerator;
import com.modive.dashboard.tools.NotFoundException;
import com.modive.dashboard.tools.ScoreCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriveDashboardWorker implements InitializingBean {

    private final BlockingQueue<IdsDto> driveQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(50); // 병렬처리 수 조정

    private final LLMService llmService;
    private final LLMRequestGenerator llmRequestGenerator;
    private final TotalDashboardService totalDashboardService;
    private final RewardClient rewardClient;
    private final ScoreCalculator scoreCalculator;
    private final DriveDashboardRepository driveDashboardRepository;
    private final DriveRepository driveRepository;

    @Override
    public void afterPropertiesSet() {
        for (int i = 0; i < 50; i++) {
            executor.submit(() -> {
                while (true) {
                    try {
                        IdsDto Ids = driveQueue.take(); // 큐에서 blocking으로 대기
                        process(Ids.getDriveId(), Ids.getUserId());
                    } catch (Exception e) {
                        System.out.println("Error processing drive analysis" + e);
                    }
                }
            });
        }
    }

    public void enqueue(String driveId, String userId) {
        driveQueue.offer(new IdsDto(driveId, userId));
    }

    private void process(String driveId, String userId) {
        DriveDashboard existing = driveDashboardRepository.findById(userId, driveId);

        if (existing != null) {
            throw new NotFoundException("[" + driveId + "]에 해당하는 주행 대시보드가 이미 생성되었습니다.");
        }

        // 1-1. 데이터 가져오기
        Drive data = driveRepository.findById(userId, driveId);
        // Drive data = getDummyDrive(userId, driveId);

        if (data == null) {
            throw new NotFoundException("[" + driveId + "]에 해당하는 주행 데이터가 없습니다.");
        }

        // 1-2. 점수 산정
        ScoreDto score = scoreCalculator.calculateDriveScore(data);

        // 1-3. 저장
        DriveDashboard dashboard = new DriveDashboard();

        dashboard.setUserId(userId);
        dashboard.setDriveId(driveId);
        dashboard.setStartTime(data.getStartTime());
        dashboard.setEndTime(data.getEndTime());
        dashboard.setScores(score);
        dashboard.setFeedbacks(null);

        List<ScoreDto> scoreList = totalDashboardService.updateTotalDashboard(userId, dashboard);
        driveDashboardRepository.save(dashboard);
        totalDashboardService.updateStatistics(data, score);
        if (Duration.between(dashboard.getStartTime(), dashboard.getEndTime()).toMinutes() > 0) EarnReward(dashboard, scoreList);
        System.out.println("점수 업데이트 완료!");
        // 1-4. 피드백 받아오기 (비동기 피드백 처리 (LLM 호출 후 DB 업데이트))
        DriveFeedbackRequest params = llmRequestGenerator.generateDriveFeedbackRequest(data);
        SingleDriveFeedbackRequest request = llmRequestGenerator.convertToSingleDriveFeedbackRequest(params);

        llmService.requestFeedbackAsync(request)
                .thenAccept(feedbacks -> {
                    DriveDashboard saved = driveDashboardRepository.findById(userId, driveId);
                    if (saved != null) {
                        saved.setFeedbacks(feedbacks);
                        driveDashboardRepository.save(saved);
                        log.info("LLM 피드백 업데이트 완료!");
                    }
                })
                .exceptionally(ex -> {
                    log.error("LLM 피드백 생성 실패: {}", ex.getMessage());
                    return null;
                });
    }

    private void EarnReward(DriveDashboard dashboard, List<ScoreDto> scoreList) {
        RewardDto.EarnComplexRequest earnComplexRequest = RewardDto.EarnComplexRequest.builder()
                .driveId(dashboard.getDriveId())
                .score((int) dashboard.getScores().getTotalScore())
                .drivingTime((int) Duration.between(dashboard.getStartTime(), dashboard.getEndTime()).toMinutes())
                .lastScore(scoreList.get(0).toScoreInfo())
                .currentScore(scoreList.get(1).toScoreInfo())
                .build();

        rewardClient.earnReward(dashboard.getUserId(), earnComplexRequest);
    }

}
