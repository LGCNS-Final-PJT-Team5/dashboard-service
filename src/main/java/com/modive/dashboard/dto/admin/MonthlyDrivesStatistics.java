package com.modive.dashboard.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Month;
import java.util.List;

@Data
@AllArgsConstructor
public class MonthlyDrivesStatistics {
    int year;
    int month;
    int count;
}
