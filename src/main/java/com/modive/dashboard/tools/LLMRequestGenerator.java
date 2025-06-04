package com.modive.dashboard.tools;

import com.modive.dashboard.dto.DriveFeedbackRequest;
import com.modive.dashboard.dto.SingleDriveFeedbackRequest;
import com.modive.dashboard.entity.Drive;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class LLMRequestGenerator {

    public DriveFeedbackRequest generateDriveFeedbackRequest(Drive drive) {
        DriveFeedbackRequest request = new DriveFeedbackRequest();

        //<editor-fold desc="# Calculate details">
        // 급가속/급감속 수
        request.setRapidAccelerationDecelerationCount(
                drive.getSuddenAccelerations() != null ? drive.getSuddenAccelerations().size() : 0
        );

        // 급회전 수
        request.setSharpTurnCount(
                drive.getSharpTurns() != null ? drive.getSharpTurns().size() : 0
        );

        // 과속 수
        request.setOverspeedCount(
                drive.getSpeedLogs() != null
                        ? (int) drive.getSpeedLogs().stream()
                        .filter(log -> log.getMaxSpeed() > 100)
                        .count() // 100km/h 초과 시 과속으로 간주
                        : 0
        );

        // 공회전 시간 (분)
        int idlingTimeMinutes = 0;
        if (drive.getIdlingPeriods() != null) {
            idlingTimeMinutes = drive.getIdlingPeriods().stream()
                    .mapToInt(p -> (int) Duration.between(p.getStartTime(), p.getEndTime()).toMinutes())
                    .sum();
        }
        request.setIdlingTimeMinutes(idlingTimeMinutes);

        // 정속 주행 비율
        if (drive.getSpeedRate() != null) {
            for (Drive.SpeedRate rate : drive.getSpeedRate()) {
                switch (rate.getTag()) {
                    case "low": // TODO: 많이 쓰이니 enum으로 만들자.
                        request.setSteadySpeedLowRatio(rate.getRatio());
                        break;
                    case "middle":
                        request.setSteadySpeedMiddleRatio(rate.getRatio());
                        break;
                    case "high":
                        request.setSteadySpeedHighRatio(rate.getRatio());
                        break;
                }
            }
        }

        // 평균 반응 시간 (밀리초)
        double avgReactionMilliSec = 0.0;
        if (drive.getReactionTimes() != null && !drive.getReactionTimes().isEmpty()) {
            avgReactionMilliSec = drive.getReactionTimes().stream()
                    .mapToLong(rt -> Duration.between(rt.getStartTime(), rt.getEndTime()).toMillis())
                    .average()
                    .orElse(0.0);
        }
        request.setAverageReactionTimeMilliSeconds(avgReactionMilliSec);

        // 차선 이탈 수
        request.setLaneDepartureCount(
                drive.getLaneDepartures() != null ? drive.getLaneDepartures().size() : 0
        );

        // 안전거리 미유지 시간 (초)
        int safeDistanceSeconds = 0;
        if (drive.getFollowingDistanceEvents() != null) {
            safeDistanceSeconds = drive.getFollowingDistanceEvents().stream()
                    .mapToInt(p -> (int) Duration.between(p.getStartTime(), p.getEndTime()).toSeconds())
                    .sum();
        }
        request.setSafeDistanceNotMaintainSeconds(safeDistanceSeconds);

        // 전체 운전 시간 (분)
        int totalDrivingMinutes = (int) Duration.between(drive.getStartTime(), drive.getEndTime()).toMinutes();
        request.setTotalDrivingMinutes(totalDrivingMinutes);

        // 미조작 횟수
        int inactiveCount = 0;
        if (drive.getInactiveMoments() != null) {
            inactiveCount = drive.getInactiveMoments().size(); // 또는 더 복잡한 계산 방식 적용 가능
        }
        request.setInactivityCount(inactiveCount);
        //</editor-fold>

        return request;
    }

    public SingleDriveFeedbackRequest convertToSingleDriveFeedbackRequest(DriveFeedbackRequest feedback) {
        Map<String, Object> params = new HashMap<>();

        params.put("rapidAccelerationDecelerationCount", feedback.getRapidAccelerationDecelerationCount());
        params.put("sharpTurnCount", feedback.getSharpTurnCount());
        params.put("overspeedCount", feedback.getOverspeedCount());
        params.put("idlingTimeMinutes", feedback.getIdlingTimeMinutes());

        params.put("steadySpeedLowRatio", feedback.getSteadySpeedLowRatio());
        params.put("steadySpeedMiddleRatio", feedback.getSteadySpeedMiddleRatio());
        params.put("steadySpeedHighRatio", feedback.getSteadySpeedHighRatio());

        params.put("averageReactionTimeMilliSeconds", feedback.getAverageReactionTimeMilliSeconds());

        params.put("laneDepartureCount", feedback.getLaneDepartureCount());
        params.put("safeDistanceNotMaintainSeconds", feedback.getSafeDistanceNotMaintainSeconds());

        params.put("totalDrivingMinutes", feedback.getTotalDrivingMinutes());
        params.put("inactivityCount", feedback.getInactivityCount());

        return new SingleDriveFeedbackRequest(params);
    }

}
