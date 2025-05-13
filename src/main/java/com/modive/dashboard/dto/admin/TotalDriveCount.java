package com.modive.dashboard.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalDriveCount {
    public int value;
    public double changeRate;
}
