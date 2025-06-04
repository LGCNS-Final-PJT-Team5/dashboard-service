package com.modive.dashboard.dto.admin;

import lombok.Data;

import java.util.List;

@Data
public class UserIdListRequest {
    private List<String> userIds;
}
