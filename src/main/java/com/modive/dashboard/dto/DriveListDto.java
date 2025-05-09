package com.modive.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriveListDto {
    private String driveId;
    private Instant startTime;
    private Instant endTime;
    private double score;
}
