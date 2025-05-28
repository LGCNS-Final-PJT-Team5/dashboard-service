package com.modive.dashboard.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.modive.dashboard.entity.TotalDashboard;
import com.modive.dashboard.entity.WeeklyDashboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WeeklyDashboardRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public void save(WeeklyDashboard weeklyDashboard) {
        dynamoDBMapper.save(weeklyDashboard);
    }

    public WeeklyDashboard findById(String dashboardId) {
        return dynamoDBMapper.load(WeeklyDashboard.class, dashboardId);
    }
}
