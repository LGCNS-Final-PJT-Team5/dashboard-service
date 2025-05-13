package com.modive.dashboard.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.modive.dashboard.entity.Statistics;
import com.modive.dashboard.entity.TotalDashboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StatisticsRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public void save(Statistics statistics) {
        dynamoDBMapper.save(statistics);
    }

    public Statistics find(String statId) {
        return dynamoDBMapper.load(Statistics.class, statId);
    }

}