package com.modive.dashboard.service;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.modive.dashboard.client.LLMClient;
import com.modive.dashboard.dto.*;
import com.modive.dashboard.dto.detail.*;
import com.modive.dashboard.entity.Drive;
import com.modive.dashboard.entity.DriveDashboard;
import com.modive.dashboard.enums.ScoreType;
import com.modive.dashboard.repository.DriveDashboardRepository;
import com.modive.dashboard.repository.DriveRepository;
import com.modive.dashboard.tools.LLMRequestGenerator;
import com.modive.dashboard.tools.ScoreCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PostDriveDashboardServiceImpl implements PostDriveDashboardService {
    @Autowired
    private DriveRepository driveRepository;
    @Autowired
    private ScoreCalculator scoreCalculator;
    @Autowired
    private DriveDashboardRepository driveDashboardRepository;
    @Autowired
    private LLMClient llmClient;
    @Autowired
    private LLMRequestGenerator llmRequestGenerator;
    @Autowired
    private TotalDashboardService totalDashboardService;

    // 1. 주행 후 대시보드 생성
    @Override
    public void createPostDriveDashboard(String userId, String driveId, Drive drive) {

        // 1-1. 데이터 가져와서 저장
        Drive dummyDrive = getDummyDrive(userId, driveId); // TODO: analysis service에서 받기
        driveRepository.save(dummyDrive);

        // 1-2. 점수 산정
        ScoreDto score = scoreCalculator.calculateDriveScore(dummyDrive);

        // 1-3. 피드백 받아오기
        DriveFeedbacksDto feedbacks = new DriveFeedbacksDto();
        // TODO: LLM 서비스에서 피드백 받아오기
        DriveFeedbackRequest params = llmRequestGenerator.generateDriveFeedbackRequest(dummyDrive);
        System.out.println(params); //임시
//        DriveFeedbacksDto feedbacks = llmClient.getDriveFeedbacks(params);

        // 1-4. 저장
        DriveDashboard dashboard = new DriveDashboard();

        dashboard.setUserId(userId);
        dashboard.setDriveId(driveId);
        dashboard.setStartTime(dummyDrive.getStartTime());
        dashboard.setEndTime(dummyDrive.getEndTime());
        dashboard.setScores(score);
        dashboard.setFeedbacks(feedbacks);

        driveDashboardRepository.save(dashboard);
        // 비동기 처리하자.
        totalDashboardService.updateTotalDashboard(userId, dashboard);
        totalDashboardService.updateStatistics(dummyDrive, score);
    }

    // 2. 주행 후 대시보드 조회
    @Override
    public DriveDashboardResponse getPostDriveDashboard(String userId, String driveId) {

        DriveDashboard dashboard = driveDashboardRepository.findById(userId, driveId);
        DriveDashboardResponse dashboardResponse = new DriveDashboardResponse(dashboard);
        return dashboardResponse;
    }

    // 3. 주행 후 대시보드 상세 조회 (safe, eco, prevention, attention)
    @Override
    public DriveDetailDto getPostDriveDashboardByType(String userId, String driveId, ScoreType type) {

        return switch (type) {
            case ECO -> getEcoDetail(userId, driveId);
            case SAFE -> getSafeDetail(userId, driveId);
            case ATTENTION -> getAttentionDetail(userId, driveId);
            case PREVENTION -> getPreventionDetail(userId, driveId);
        };

    }

    //<editor-folder desc="# Get detail dto">
    private EcoDetailDto getEcoDetail(String userId, String driveId) {
        EcoDetailDto dto = new EcoDetailDto();
        DriveDashboard dashboard = driveDashboardRepository.findById(userId, driveId);
        Drive drive = driveRepository.findById(userId, driveId);

        SubScoreDto idling = new SubScoreDto();
        SubScoreDto speedMaintain = new SubScoreDto();


        idling.score = dashboard.getScores().idlingScore;
        speedMaintain.score = dashboard.getScores().speedMaintainScore;

        idling.feedback = dashboard.getFeedbacks().idlingFeedback;
        speedMaintain.feedback = dashboard.getFeedbacks().speedMaintainFeedback;

        idling.graph = drive.getIdlingPeriods();
        speedMaintain.graph = drive.getSpeedRate();

        dto.score = dashboard.getScores().ecoScore;
        dto.idling = idling;
        dto.speedMaintain = speedMaintain;

        return dto;
    }
    private SafeDetailDto getSafeDetail(String userId, String driveId) {
        SafeDetailDto dto = new SafeDetailDto();
        DriveDashboard dashboard = driveDashboardRepository.findById(userId, driveId);
        Drive drive = driveRepository.findById(userId, driveId);

        SubScoreDto acceleration = new SubScoreDto();
        SubScoreDto sharpTurn = new SubScoreDto();
        SubScoreDto overSpeed  = new SubScoreDto();

        acceleration.score = dashboard.getScores().accelerationScore;
        sharpTurn.score = dashboard.getScores().sharpTurnScore;
        overSpeed.score = dashboard.getScores().overSpeedScore;

        acceleration.feedback = dashboard.getFeedbacks().accelerationFeedback;
        sharpTurn.feedback = dashboard.getFeedbacks().sharpTurnFeedback;
        overSpeed.feedback = dashboard.getFeedbacks().overSpeedFeedback;

        acceleration.graph = drive.getSuddenAccelerations();
        sharpTurn.graph = drive.getSharpTurns();
        overSpeed.graph = drive.getSpeedLogs();

        dto.score = dashboard.getScores().safetyScore;
        dto.acceleration = acceleration;
        dto.sharpTurn = sharpTurn;
        dto.overSpeed = overSpeed;

        return dto;
    }
    private AttentionDetailDto getAttentionDetail(String userId, String driveId) {
        AttentionDetailDto dto = new AttentionDetailDto();
        DriveDashboard dashboard = driveDashboardRepository.findById(userId, driveId);
        Drive drive = driveRepository.findById(userId, driveId);

        SubScoreDto drivingTime = new SubScoreDto();
        SubScoreDto inactivity = new SubScoreDto();

        drivingTime.score = dashboard.getScores().drivingTimeScore;
        inactivity.score = dashboard.getScores().inactivityScore;

        drivingTime.feedback = dashboard.getFeedbacks().drivingTimeFeedback;
        inactivity.feedback = dashboard.getFeedbacks().inactivityFeedback;

        drivingTime.graph = List.of(
                Map.of("startTime", drive.getStartTime(), "endTime", drive.getEndTime()));
        inactivity.graph = drive.getInactiveMoments();

        dto.score = dashboard.getScores().attentionScore;
        dto.drivingTime = drivingTime;
        dto.inactivity = inactivity;

        return dto;
    }
    private PreventDetailDto getPreventionDetail(String userId, String driveId) {
        PreventDetailDto dto = new PreventDetailDto();
        DriveDashboard dashboard = driveDashboardRepository.findById(userId, driveId);
        Drive drive = driveRepository.findById(userId, driveId);

        SubScoreDto reaction = new SubScoreDto();
        SubScoreDto laneDeparture = new SubScoreDto();
        SubScoreDto followingDistance  = new SubScoreDto();

        reaction.score = dashboard.getScores().reactionScore;
        laneDeparture.score = dashboard.getScores().laneDepartureScore;
        followingDistance.score = dashboard.getScores().followingDistanceScore;

        reaction.feedback = dashboard.getFeedbacks().reactionFeedback;
        laneDeparture.feedback = dashboard.getFeedbacks().laneDepartureFeedback;
        followingDistance.feedback = dashboard.getFeedbacks().followingDistanceFeedback;

        reaction.graph = drive.getReactionTimes();
        laneDeparture.graph = drive.getLaneDepartures();
        followingDistance.graph = drive.getFollowingDistanceEvents();

        dto.score = dashboard.getScores().accidentPreventionScore;
        dto.reaction = reaction;
        dto.laneDeparture = laneDeparture;
        dto.followingDistance = followingDistance;

        return dto;
    }
    //</editor-folder>

    // 4. 주행 후 대시보드 목록 조회
    @Override
    public PaginatedListResponse<DriveListDto> getPostDriveDashboardList(String userId, String startTime, String driveId, int pageSize) {

        Map<String, AttributeValue> lastEvaluatedKey = null;
        if (startTime != null && userId != null) {
            lastEvaluatedKey = Map.of(
                    "userId", new AttributeValue().withS(userId),
                    "startTime", new AttributeValue().withS(startTime),
                    "driveId", new AttributeValue().withS(driveId)
            );
        }

        PaginatedListResponse<DriveListDto> response = driveDashboardRepository.listByUserId(userId, pageSize, lastEvaluatedKey);

        return new PaginatedListResponse<DriveListDto>(response.getList(), response.getDriveId(), response.getStartTime());
    }

    // <editor-fold desc="# Get dummy data">
    private Drive getDummyDrive(String userId, String driveId) {
        Instant startTime = Instant.now();
        long randomMinutes = ThreadLocalRandom.current().nextLong(10, 61); // 10 ~ 60분
        Instant endTime = startTime.plus(Duration.ofMinutes(randomMinutes));

        Random random = new Random();

        Drive drive = new Drive();
        drive.setUserId(userId);
        drive.setDriveId(driveId);
        drive.setStartTime(startTime);
        drive.setEndTime(endTime);
        drive.setActiveDriveDurationSec(6900);

        // SuddenAccelerations
        drive.setSuddenAccelerations(generateRandomInstants(startTime, endTime, 5));

        // SharpTurns
        drive.setSharpTurns(generateRandomInstants(startTime, endTime, 8));

        // SpeedLogs
        List<Drive.SpeedLog> speedLogs = new ArrayList<>();
        for (int i = 1; i <= 21; i++) {
            Drive.SpeedLog log = new Drive.SpeedLog();
            log.setPeriod(i);
            log.setMaxSpeed(30 + random.nextInt(90));  // 30~120 km/h
            speedLogs.add(log);
        }
        drive.setSpeedLogs(speedLogs);

        // IdlingPeriods
        List<Drive.StartEndTime> idlingPeriods = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Instant s = startTime.plusSeconds(i * 600);
            Drive.StartEndTime p = new Drive.StartEndTime(s,s.plusSeconds(120) );
            idlingPeriods.add(p);
        }
        drive.setIdlingPeriods(idlingPeriods);

        // SpeedRate
        List<Drive.SpeedRate> speedRateList = List.of(
                new Drive.SpeedRate("high", 10),
                new Drive.SpeedRate("middle", 65),
                new Drive.SpeedRate("low", 25)
        );
        drive.setSpeedRate(speedRateList);

        // ReactionTimes
        List<Drive.StartEndTime> reactionTimes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Instant d = startTime.plusSeconds(i * 1020);
            Drive.StartEndTime r = new Drive.StartEndTime(d, d.plusSeconds(2));
            reactionTimes.add(r);
        }
        drive.setReactionTimes(reactionTimes);

        // LaneDepartures
        drive.setLaneDepartures(generateRandomInstants(startTime, endTime, 6));

        // FollowingDistanceEvents
        List<Drive.StartEndTime> distanceTimes = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Instant d = startTime.plusSeconds(i * 1020);
            Drive.StartEndTime r = new Drive.StartEndTime(d, d.plusSeconds(2));
            distanceTimes.add(r);
        }
        drive.setFollowingDistanceEvents(distanceTimes);

        // InactiveMoments
        drive.setInactiveMoments(generateRandomInstants(startTime, endTime, 6));

        return drive;
    }
    private List<Instant> generateRandomInstants(Instant start, Instant end, int count) {
        List<Instant> instants = new ArrayList<>();
        long startEpoch = start.getEpochSecond();
        long endEpoch = end.getEpochSecond();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            long randomEpoch = startEpoch + (long) (random.nextDouble() * (endEpoch - startEpoch));
            instants.add(Instant.ofEpochSecond(randomEpoch));
        }

        instants.sort(Comparator.naturalOrder());
        return instants;
    }
    // </editor-fold>
}
