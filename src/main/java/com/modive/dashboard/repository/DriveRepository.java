package com.modive.dashboard.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.modive.dashboard.dto.DriveListDto;
import com.modive.dashboard.entity.Drive;
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
public class DriveRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public void save(Drive drive) {
        dynamoDBMapper.save(drive);
    }

    public Drive findById(String userId, String driveId) {
        return dynamoDBMapper.load(Drive.class, userId, driveId);
    }

//    public List<DriveListDto> listByUserId(String userId) {
//        // 1. 키 객체 생성 (userId만 설정)
//        Drive keyObject = new Drive();
//        keyObject.setUserId(userId);
//
//        // 2. 쿼리 조건 설정
//        DynamoDBQueryExpression<Drive> queryExpression = new DynamoDBQueryExpression<Drive>()
//                .withHashKeyValues(keyObject);
//
//        // 3. 쿼리 실행
//        List<Drive> drives = dynamoDBMapper.query(Drive.class, queryExpression);
//
//        // 4. 필요한 필드만 DriveListDto로 매핑
//        return drives.stream()
//                .map(d -> new DriveListDto(d.getDriveId(), d.getStartTime(), d.getEndTime()))
//                .collect(Collectors.toList());
//    }

    public void deleteById(String driveId) {
        Drive drive = new Drive();
        drive.setDriveId(driveId);
        dynamoDBMapper.delete(drive);
    }

    public List<Drive> findAll() {
        return dynamoDBMapper.scan(Drive.class, new DynamoDBScanExpression());
    }

    // 특정 기간 이후 시작된 Drive 조회
    public List<Drive> findByStartTimeAfter(String isoStartTime) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":startTime", new AttributeValue().withS(isoStartTime));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("startTime > :startTime")
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(Drive.class, scanExpression);
    }
}