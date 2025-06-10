package com.modive.dashboard.dto;

import com.modive.dashboard.enums.UserType;
import lombok.Data;

@Data
public class ReportRequest {

    public String userId;                   // 사용자 ID
    public String userType;
    public ScoreDto scores;
}
