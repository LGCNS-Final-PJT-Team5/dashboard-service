package com.modive.dashboard.dto;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedListResponse<T> {
    private List<T> list;
    private String driveId;
    private String startTime;
    //    private Map<String, AttributeValue> lastEvaluatedKey;
}
