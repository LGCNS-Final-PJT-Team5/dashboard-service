package com.modive.dashboard.dto;

import com.modive.dashboard.dto.admin.DriveHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.*;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriveListDto {
    private String driveId;
    private Instant startTime;
    private Instant endTime;
    private double score;

    public DriveHistory toDriveHistory() {
        DriveHistory driveHistory = new DriveHistory();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = startTime.atZone(ZoneId.systemDefault()).toLocalDate();
        String formattedDate = localDate.format(formatter);

        driveHistory.setDriveId(driveId);
        driveHistory.setDate(formattedDate);
        driveHistory.setDriveDuration((int) Duration.between(startTime, endTime).toMinutes());

        return driveHistory;
    }
}
