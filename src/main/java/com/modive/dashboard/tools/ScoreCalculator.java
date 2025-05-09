package com.modive.dashboard.tools;

import com.modive.dashboard.dto.ScoreDto;
import com.modive.dashboard.entity.Drive;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class ScoreCalculator {

    public ScoreDto calculateDriveScore(Drive drive) {
        ScoreDto score = new ScoreDto();

        // === 탄소 배출 및 연비 점수 ===
        score.idlingScore = calcIdlingScore(drive);
        score.speedMaintainScore = calcSpeedMaintainScore(drive);
        score.ecoScore = (score.idlingScore + score.speedMaintainScore) / 2.0;

        // === 안전 운전 점수 ===
        score.accelerationScore = calcAccelerationScore(drive);
        score.sharpTurnScore = calcSharpTurnScore(drive);
        score.overSpeedScore = calcOverSpeedScore(drive);
        score.safetyScore = (score.accelerationScore + score.sharpTurnScore + score.overSpeedScore) / 3.0;

        // === 사고 예방 점수 ===
        score.reactionScore = calcReactionTimeScore(drive);
        score.laneDepartureScore = calcLaneDepartureScore(drive);
        score.followingDistanceScore = calcFollowingDistanceScore(drive);
        score.accidentPreventionScore = (score.reactionScore + score.laneDepartureScore + score.followingDistanceScore) / 3.0;

        // === 주의력 점수 ===
        score.drivingTimeScore = calcDrivingTimeScore(drive);
        score.inactivityScore = calcInactivityScore(drive);
        score.attentionScore = (score.drivingTimeScore + score.inactivityScore) / 2.0;

        // === 총점 ===
        score.totalScore = (score.ecoScore + score.safetyScore + score.accidentPreventionScore + score.attentionScore) / 4.0;

        return score;
    }

    // <editor-fold desc="# Get detail score">
    // 탄소 배출 점수: 공회전
    private double calcIdlingScore(Drive drive) {
        int score = 100;
        for (Drive.StartEndTime period : drive.getIdlingPeriods()) {
            long seconds = Duration.between(period.getStartTime(), period.getEndTime()).getSeconds();
            if (seconds >= 120) {
                score -= (int) ((seconds - 120) / 30) * 5;
            }
        }
        return Math.max(score, 0);
    }

    // 탄소 배출 점수: 정속주행
    private double calcSpeedMaintainScore(Drive drive) {
        return drive.getSpeedRate().stream()
                .filter(sr -> "middle".equals(sr.getTag()))
                .map(sr -> sr.getRatio())
                .findFirst()
                .orElse(100);
    }

    // 안전운전 점수: 급가속/급감속
    private double calcAccelerationScore(Drive drive) {
            List<Drive.TimeWithFlag> accelerations = drive.getSuddenAccelerations();

            long b = accelerations.size();
            if (b == 0) return 100;

            long a = accelerations.stream()
                    .filter(t -> t.isFlag())
                    .count();

            return 100.0 * a / b;
        }

    // 안전운전 점수: 급회전
    private double calcSharpTurnScore(Drive drive) {
        List<Drive.TimeWithFlag> sharpTurns = drive.getSharpTurns();

        long b = sharpTurns.size();
        if (b == 0) return 100;

        long a = sharpTurns.stream()
                .filter(t -> t.isFlag())
                .count();

        return 100.0 * a / b;
    }

    // 안전운전 점수: 과속 (횟수당 감점)
    private double calcOverSpeedScore(Drive drive) {
        int score = 100;
        for (Drive.SpeedLog log : drive.getSpeedLogs()) {
            if (log.getMaxSpeed() >= 100) {
                score -= 10;
            }
            if (log.getMaxSpeed() >= 110) {
                score -= 5;
            }
            if (log.getMaxSpeed() >= 120) {
                score -= 5;
            }
        }
        return Math.max(score, 0);
    }

    // 사고 예방 점수: 반응속도
    private double calcReactionTimeScore(Drive drive) {
        double x = 0, y = 0;
        for (Drive.StartEndTime rt : drive.getReactionTimes()) {
            double delta = Duration.between(rt.getStartTime(), rt.getEndTime()).toMillis() / 1000.0;
            if (delta < 0.9) x++;
            else y++;
        }
        return (x + y == 0) ? 100 : 100.0 * x / (x + y);
    }

    // 사고 예방 점수: 차선이탈
    private double calcLaneDepartureScore(Drive drive) {
        return Math.max(0, 100 - (10 * drive.getLaneDepartures().size()));
    }

    // 사고 예방 점수: 안전거리 유지 (초당 3점 감점)
    private double calcFollowingDistanceScore(Drive drive) {
        long totalSeconds = drive.getFollowingDistanceEvents().stream()
                .mapToLong(event -> {
                    if (event.getStartTime() != null && event.getEndTime() != null) {
                        return Duration.between(event.getStartTime(), event.getEndTime()).getSeconds();
                    } else {
                        return 0;
                    }
                })
                .sum();

        return Math.max(0, 100 - (3 * totalSeconds));
    }

    // 주의력 점수: 운전 시간
    private double calcDrivingTimeScore(Drive drive) {
        long driveMinutes = Duration.between(drive.getStartTime(), drive.getEndTime()).toMinutes();
        if (driveMinutes <= 120) return 100;
        return Math.max(0, 100 - ((driveMinutes - 120) / 10) * 5);
    }

    // 주의력 점수: 미조작 시간 (횟수당 10점 감점)
    private double calcInactivityScore(Drive drive) {
        int count = drive.getInactiveMoments().size();
        return Math.max(0, 100 - (count * 10));
    }
    // </editor-fold>
}
