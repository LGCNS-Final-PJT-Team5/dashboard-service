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
        if (drive.getIdlingPeriods() == null || drive.getIdlingPeriods().isEmpty()) {
            return score;
        }
        for (Drive.StartEndTime period : drive.getIdlingPeriods()) {
            if (period == null || period.getStartTime() == null || period.getEndTime() == null) continue;
            long seconds = Duration.between(period.getStartTime(), period.getEndTime()).getSeconds();
            if (seconds >= 120) {
                score -= (int) ((seconds - 120) / 30) * 5;
            }
        }
        return Math.max(score, 0);
    }

    // 탄소 배출 점수: 정속주행
    private double calcSpeedMaintainScore(Drive drive) {
        if (drive.getSpeedRate() == null || drive.getSpeedRate().isEmpty()) {
            return 100;
        }
        return drive.getSpeedRate().stream()
                .filter(sr -> sr != null && "middle".equals(sr.getTag()))
                .map(sr -> sr.getRatio())
                .findFirst()
                .orElse(100);
    }

    // 안전운전 점수: 급가속/급감속
    private double calcAccelerationScore(Drive drive) {
        if (drive.getSuddenAccelerations() == null) {
            return 100;
        }
        return Math.max(0, 100 - (10 * drive.getSuddenAccelerations().size()));
    }

    // 안전운전 점수: 급회전
    private double calcSharpTurnScore(Drive drive) {
        if (drive.getSharpTurns() == null) {
            return 100;
        }
        return Math.max(0, 100 - (10 * drive.getSharpTurns().size()));
    }

    // 안전운전 점수: 과속 (횟수당 감점)
    private double calcOverSpeedScore(Drive drive) {
        int score = 100;
        if (drive.getSpeedLogs() == null || drive.getSpeedLogs().isEmpty()) {
            return score;
        }
        for (Drive.SpeedLog log : drive.getSpeedLogs()) {
            if (log == null) continue;
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
        List<Drive.StartEndTime> reactionTimes = drive.getReactionTimes();
        if (reactionTimes == null || reactionTimes.isEmpty()) {
            return 100.0;
        }

        double x = 0, y = 0;
        for (Drive.StartEndTime rt : reactionTimes) {
            if (rt == null || rt.getStartTime() == null || rt.getEndTime() == null) continue;
            double delta = Duration.between(rt.getStartTime(), rt.getEndTime()).toMillis() / 1000.0;
            if (delta < 0.9) x++;
            else y++;
        }
        return (x + y == 0) ? 100 : 100.0 * x / (x + y);
    }

    // 사고 예방 점수: 차선이탈
    private double calcLaneDepartureScore(Drive drive) {
        if (drive.getLaneDepartures() == null) {
            return 100;
        }
        return Math.max(0, 100 - (10 * drive.getLaneDepartures().size()));
    }

    // 사고 예방 점수: 안전거리 미유지 (초당 3점 감점)
    private double calcFollowingDistanceScore(Drive drive) {
        List<Drive.StartEndTime> events = drive.getFollowingDistanceEvents();
        if (events == null || events.isEmpty()) {
            return 100.0;
        }

        long totalSeconds = events.stream()
                .filter(event -> event != null && event.getStartTime() != null && event.getEndTime() != null)
                .mapToLong(event -> Duration.between(event.getStartTime(), event.getEndTime()).getSeconds())
                .sum();

        return Math.max(0, 100 - (3 * totalSeconds));
    }

    // 주의력 점수: 운전 시간
    private double calcDrivingTimeScore(Drive drive) {
        if (drive.getStartTime() == null || drive.getEndTime() == null) {
            return 100;
        }
        long driveMinutes = Duration.between(drive.getStartTime(), drive.getEndTime()).toMinutes();
        if (driveMinutes <= 120) return 100;
        return Math.max(0, 100 - ((driveMinutes - 120) / 10) * 5);
    }

    // 주의력 점수: 미조작 시간 (횟수당 10점 감점)
    private double calcInactivityScore(Drive drive) {
        if (drive.getInactiveMoments() == null) {
            return 100;
        }
        int count = drive.getInactiveMoments().size();
        return Math.max(0, 100 - (count * 10));
    }

    // </editor-fold>

    public ScoreDto calculateTotalScore(ScoreDto totalScore, ScoreDto driveScore, int totalDriveCount) {
        if (totalDriveCount <= 0) {
            return driveScore;
        }

        totalScore.idlingScore = (totalScore.idlingScore * (totalDriveCount - 1) + driveScore.idlingScore) / totalDriveCount;
        totalScore.speedMaintainScore = (totalScore.speedMaintainScore * (totalDriveCount - 1) + driveScore.speedMaintainScore) / totalDriveCount;
        totalScore.ecoScore = (totalScore.ecoScore * (totalDriveCount - 1) + driveScore.ecoScore) / totalDriveCount;

        totalScore.accelerationScore = (totalScore.accelerationScore * (totalDriveCount - 1) + driveScore.accelerationScore) / totalDriveCount;
        totalScore.sharpTurnScore = (totalScore.sharpTurnScore * (totalDriveCount - 1) + driveScore.sharpTurnScore) / totalDriveCount;
        totalScore.overSpeedScore = (totalScore.overSpeedScore * (totalDriveCount - 1) + driveScore.overSpeedScore) / totalDriveCount;
        totalScore.safetyScore = (totalScore.safetyScore * (totalDriveCount - 1) + driveScore.safetyScore) / totalDriveCount;

        totalScore.reactionScore = (totalScore.reactionScore * (totalDriveCount - 1) + driveScore.reactionScore) / totalDriveCount;
        totalScore.laneDepartureScore = (totalScore.laneDepartureScore * (totalDriveCount - 1) + driveScore.laneDepartureScore) / totalDriveCount;
        totalScore.followingDistanceScore = (totalScore.followingDistanceScore * (totalDriveCount - 1) + driveScore.followingDistanceScore) / totalDriveCount;
        totalScore.accidentPreventionScore = (totalScore.accidentPreventionScore * (totalDriveCount - 1) + driveScore.accidentPreventionScore) / totalDriveCount;

        totalScore.drivingTimeScore = (totalScore.drivingTimeScore * (totalDriveCount - 1) + driveScore.drivingTimeScore) / totalDriveCount;
        totalScore.inactivityScore = (totalScore.inactivityScore * (totalDriveCount - 1) + driveScore.inactivityScore) / totalDriveCount;
        totalScore.attentionScore = (totalScore.attentionScore * (totalDriveCount - 1) + driveScore.attentionScore) / totalDriveCount;

        totalScore.totalScore = (totalScore.totalScore * (totalDriveCount - 1) + driveScore.totalScore) / totalDriveCount;

        return totalScore;
    }


    public ScoreDto calculateAverageScore(List<ScoreDto> scores) {
        if (scores == null || scores.isEmpty()) {
            return new ScoreDto(); // 모든 값이 0.0인 기본 객체 반환
        }

        ScoreDto avg = new ScoreDto();
        int count = scores.size();

        for (ScoreDto s : scores) {
            avg.idlingScore += s.idlingScore;
            avg.speedMaintainScore += s.speedMaintainScore;
            avg.ecoScore += s.ecoScore;

            avg.accelerationScore += s.accelerationScore;
            avg.sharpTurnScore += s.sharpTurnScore;
            avg.overSpeedScore += s.overSpeedScore;
            avg.safetyScore += s.safetyScore;

            avg.reactionScore += s.reactionScore;
            avg.laneDepartureScore += s.laneDepartureScore;
            avg.followingDistanceScore += s.followingDistanceScore;
            avg.accidentPreventionScore += s.accidentPreventionScore;

            avg.drivingTimeScore += s.drivingTimeScore;
            avg.inactivityScore += s.inactivityScore;
            avg.attentionScore += s.attentionScore;

            avg.totalScore += s.totalScore;
        }

        // 평균 계산
        avg.idlingScore /= count;
        avg.speedMaintainScore /= count;
        avg.ecoScore /= count;

        avg.accelerationScore /= count;
        avg.sharpTurnScore /= count;
        avg.overSpeedScore /= count;
        avg.safetyScore /= count;

        avg.reactionScore /= count;
        avg.laneDepartureScore /= count;
        avg.followingDistanceScore /= count;
        avg.accidentPreventionScore /= count;

        avg.drivingTimeScore /= count;
        avg.inactivityScore /= count;
        avg.attentionScore /= count;

        avg.totalScore /= count;

        return avg;
    }


}
