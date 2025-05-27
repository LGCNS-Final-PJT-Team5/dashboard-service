package com.modive.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RewardDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EarnComplexRequest {
        private Long driveId;
        private Integer score;

        @JsonProperty("주행 시간")
        private Integer drivingTime;

        private ScoreInfo lastScore;

        private ScoreInfo currentScore;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ScoreInfo {
            private Integer carbon;
            private Integer safety;
            private Integer accident;
            private Integer focus;
        }

    }
}