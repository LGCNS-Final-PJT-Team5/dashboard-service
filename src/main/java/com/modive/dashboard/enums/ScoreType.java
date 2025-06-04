package com.modive.dashboard.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ScoreType {
    // 1. 안전 운전 점수
    SAFE,

    // 2. 탄소 배출 점수
    ECO,

    // 3. 사고 예방 점수
    PREVENTION,

    // 4. 주의력 점수
    ATTENTION;

    @JsonCreator
    public static ScoreType fromString(String value) {
        return ScoreType.valueOf(value.toUpperCase());
    }

}
