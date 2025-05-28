package com.modive.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ReportResponse {
    public Feedbacks data;
    public int status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Feedbacks {
        public ReportDto.TotalFeedback totalFeedback;
        public ReportDto.DetailedFeedback detailedFeedback;
    }
}
