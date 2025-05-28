package com.modive.dashboard.client;

import com.modive.dashboard.dto.ReportRequest;
import com.modive.dashboard.dto.ReportResponse;
import com.modive.dashboard.dto.RewardDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "http://localhost:8080/report")
public interface ReportClient {

    @PostMapping()
    ReportResponse getReport(@RequestBody ReportRequest request);

}
