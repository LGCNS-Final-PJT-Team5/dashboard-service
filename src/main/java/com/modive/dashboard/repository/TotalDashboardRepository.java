package com.modive.dashboard.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.modive.dashboard.dto.DriveListDto;
import com.modive.dashboard.entity.DriveDashboard;
import com.modive.dashboard.entity.TotalDashboard;
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
public class TotalDashboardRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public void save(TotalDashboard totalDashboard) {
        dynamoDBMapper.save(totalDashboard);
    }

    public TotalDashboard findById(String userId) {
        return dynamoDBMapper.load(TotalDashboard.class, userId);
    }

    public void deleteById(String userId) {
        TotalDashboard dashboard = new TotalDashboard();
        dashboard.setUserId(userId);
        dynamoDBMapper.delete(dashboard);
    }

    public List<TotalDashboard> findAll() {
        return dynamoDBMapper.scan(TotalDashboard.class, new DynamoDBScanExpression());
    }

}