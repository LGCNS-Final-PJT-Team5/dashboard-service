package com.modive.dashboard.client;

import com.modive.dashboard.dto.ReportRequest;
import com.modive.dashboard.dto.ReportResponse;
import com.modive.dashboard.dto.RewardDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "report-service", url = "http://modive.site:60010")
public interface ReportClient {

    @PostMapping("/agent/weekly")
    ReportResponse getReport(@RequestBody ReportRequest request);

}
