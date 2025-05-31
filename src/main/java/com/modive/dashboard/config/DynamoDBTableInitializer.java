package com.modive.dashboard.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
public class DynamoDBTableInitializer {
    private final AmazonDynamoDB dynamoDB;

    public DynamoDBTableInitializer(@Value("${aws.region}") String region) {
        this.dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withRegion(region)
                .build();
    }

    @PostConstruct
    public void createDriveIfNotExists() {
        ListTablesResult tables = dynamoDB.listTables();
        if (!tables.getTableNames().contains("drive")) {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("drive")
                    .withKeySchema(
                        new KeySchemaElement("userId", KeyType.HASH),
                        new KeySchemaElement("driveId", KeyType.RANGE)
                    )
                    .withAttributeDefinitions(
                        new AttributeDefinition("userId", ScalarAttributeType.S),
                        new AttributeDefinition("driveId", ScalarAttributeType.S)
                    )
                    .withBillingMode(BillingMode.PAY_PER_REQUEST);

            dynamoDB.createTable(request);
            System.out.println("✅ DynamoDB 테이블 'drive' 생성됨");
        } else {
            System.out.println("ℹ️ DynamoDB 테이블 'drive' 이미 존재함");
        }
    }

    @PostConstruct
    public void createDriveDashboardIfNotExists() {
        ListTablesResult tables = dynamoDB.listTables();
        if (!tables.getTableNames().contains("drive-dashboard")) {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("drive-dashboard")
                    .withKeySchema(
                            new KeySchemaElement("userId", KeyType.HASH),
                            new KeySchemaElement("driveId", KeyType.RANGE)
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("userId", ScalarAttributeType.S),
                            new AttributeDefinition("driveId", ScalarAttributeType.S),
                            new AttributeDefinition("startTime", ScalarAttributeType.S)
                    )
                    .withGlobalSecondaryIndexes(
                            new GlobalSecondaryIndex()
                                    .withIndexName("userId-startTime-index")
                                    .withKeySchema(
                                            new KeySchemaElement("userId", KeyType.HASH),      // GSI의 Partition Key
                                            new KeySchemaElement("startTime", KeyType.RANGE)   // GSI의 Sort Key
                                    )
                                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL)) // 전체 속성 포함
                    )
                    .withBillingMode(BillingMode.PAY_PER_REQUEST);

            dynamoDB.createTable(request);
            System.out.println("✅ DynamoDB 테이블 'drive-dashboard' 생성됨");
        } else {
            System.out.println("ℹ️ DynamoDB 테이블 'drive-dashboard' 이미 존재함");
        }
    }

    @PostConstruct
    public void createTotalDashboardIfNotExists() {
        ListTablesResult tables = dynamoDB.listTables();
        if (!tables.getTableNames().contains("total-dashboard")) {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("total-dashboard")
                    .withKeySchema(
                            new KeySchemaElement("userId", KeyType.HASH)
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("userId", ScalarAttributeType.S)
                    )
                    .withBillingMode(BillingMode.PAY_PER_REQUEST);

            dynamoDB.createTable(request);
            System.out.println("✅ DynamoDB 테이블 'total-dashboard' 생성됨");
        } else {
            System.out.println("ℹ️ DynamoDB 테이블 'total-dashboard' 이미 존재함");
        }
    }

    @PostConstruct
    public void createSummaryDashboardIfNotExists() {
        ListTablesResult tables = dynamoDB.listTables();
        if (!tables.getTableNames().contains("statistics")) {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("statistics")
                    .withKeySchema(
                            new KeySchemaElement("statId", KeyType.HASH)
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("statId", ScalarAttributeType.S)
                    )
                    .withBillingMode(BillingMode.PAY_PER_REQUEST);

            dynamoDB.createTable(request);
            System.out.println("✅ DynamoDB 테이블 'statistics' 생성됨");
        } else {
            System.out.println("ℹ️ DynamoDB 테이블 'statistics' 이미 존재함");
        }
    }

    @PostConstruct
    public void createWeeklyDashboardIfNotExists() {
        ListTablesResult tables = dynamoDB.listTables();
        if (!tables.getTableNames().contains("weekly-dashboard")) {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("weekly-dashboard")
                    .withKeySchema(
                            new KeySchemaElement("dashboardId", KeyType.HASH)
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("dashboardId", ScalarAttributeType.S)
                    )
                    .withBillingMode(BillingMode.PAY_PER_REQUEST);

            dynamoDB.createTable(request);
            System.out.println("✅ DynamoDB 테이블 'weekly-dashboard' 생성됨");
        } else {
            System.out.println("ℹ️ DynamoDB 테이블 'weekly-dashboard' 이미 존재함");
        }
    }

}