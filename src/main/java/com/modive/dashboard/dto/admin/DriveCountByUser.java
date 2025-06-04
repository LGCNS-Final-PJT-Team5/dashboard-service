package com.modive.dashboard.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriveCountByUser {
    String userId;
    int driveCount;
}
