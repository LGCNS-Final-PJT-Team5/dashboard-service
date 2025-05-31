package com.modive.dashboard.client;

import com.modive.dashboard.dto.DriveFeedbacksDto;
import com.modive.dashboard.dto.RewardDto;
import com.modive.dashboard.dto.SingleDriveFeedbackRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="reward-service",
        url="${service.reard.url}")
public interface RewardClient {

    @PostMapping("/reward/earn")
    void earnReward(@RequestHeader("X-User-Id") String userId, @RequestBody RewardDto.EarnComplexRequest request);

}
