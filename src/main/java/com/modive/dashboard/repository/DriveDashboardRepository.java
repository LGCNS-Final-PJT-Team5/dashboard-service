package com.modive.dashboard.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.modive.dashboard.dto.DriveListDto;
import com.modive.dashboard.dto.PaginatedListResponse;
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

    /// userId로 운전 목록 조회하는 함수
    ///
    /// 정렬 조건 : startTime 내림차순
    ///
    /// 조회 크기 : pageSize
    ///
    /// lastEvaluatedKey : userId, driveId, startTime 모두 있어야 함
    public PaginatedListResponse<DriveListDto> listByUserId(String userId, int pageSize, Map<String, AttributeValue> lastEvaluatedKey) {

        // １. 키 캑체
        DriveDashboard hashKeyValues = new DriveDashboard();
        hashKeyValues.setUserId(userId);

        // ２. 쿼리
        DynamoDBQueryExpression<DriveDashboard> queryExpression = new DynamoDBQueryExpression<DriveDashboard>()
                .withIndexName("userId-startTime-index")
                .withHashKeyValues(hashKeyValues)
                .withConsistentRead(false)
                .withScanIndexForward(false)
                .withLimit(pageSize);

        // 3. 커서 적용
        if (lastEvaluatedKey != null) {
            queryExpression.withExclusiveStartKey(lastEvaluatedKey);
        }

        // 4. 쿼리 실행
        QueryResultPage<DriveDashboard> resultPage = dynamoDBMapper.queryPage(DriveDashboard.class, queryExpression);

        // 5. 필요한 필드만 DriveListDto로 매핑
        List<DriveListDto> items = resultPage.getResults().stream()
                .map(d -> new DriveListDto(d.getDriveId(), d.getStartTime(), d.getEndTime(), d.getScores().totalScore))
                .toList();

        String driveId = null;
        String startTime = null;

        if (resultPage.getLastEvaluatedKey() != null) {
            driveId = resultPage.getLastEvaluatedKey().get("driveId") != null ? resultPage.getLastEvaluatedKey().get("driveId").getS() : null;
            startTime = resultPage.getLastEvaluatedKey().get("startTime") != null ? resultPage.getLastEvaluatedKey().get("startTime").getS() : null;
        }

        return new PaginatedListResponse<DriveListDto>(items, driveId, startTime);
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