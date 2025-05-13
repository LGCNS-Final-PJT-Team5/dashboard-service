package com.modive.dashboard.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.modive.dashboard.dto.DriveListDto;
import com.modive.dashboard.entity.Drive;
import com.modive.dashboard.entity.DriveDashboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DriveDashboardRepository{

    private final DynamoDBMapper dynamoDBMapper;

    public void save(DriveDashboard driveDashboard) {
        dynamoDBMapper.save(driveDashboard);
    }

    public DriveDashboard findById(String userId, String driveId) {
        return dynamoDBMapper.load(DriveDashboard.class, userId, driveId);
    }

    public List<DriveListDto> listByUserId(String userId) {
        // 1. 키 객체 생성 (userId만 설정)
        DriveDashboard keyObject = new DriveDashboard();
        keyObject.setUserId(userId);

        // 2. 쿼리 조건 설정
        DynamoDBQueryExpression<DriveDashboard> queryExpression = new DynamoDBQueryExpression<DriveDashboard>()
                .withHashKeyValues(keyObject);

        // 3. 쿼리 실행
        List<DriveDashboard> dashboards = dynamoDBMapper.query(DriveDashboard.class, queryExpression);

        // 4. 필요한 필드만 DriveListDto로 매핑
        return dashboards.stream()
                .map(d -> new DriveListDto(d.getDriveId(), d.getStartTime(), d.getEndTime(), d.getScores().totalScore))
                .collect(Collectors.toList());
    }

    public void deleteById(String driveId) {
        DriveDashboard dashboard = new DriveDashboard();
        dashboard.setDriveId(driveId);
        dynamoDBMapper.delete(dashboard);
    }

    public List<DriveDashboard> findAll() {
        return dynamoDBMapper.scan(DriveDashboard.class, new DynamoDBScanExpression());
    }

    // 특정 기간 이후 시작된 Drive 조회
    public List<DriveDashboard> findByStartTimeAfter(String isoStartTime) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":startTime", new AttributeValue().withS(isoStartTime));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("startTime > :startTime")
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(DriveDashboard.class, scanExpression);
    }

}