package com.modive.dashboard.client;

import com.modive.dashboard.dto.RewardDto;
import com.modive.dashboard.enums.UserType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="user-service",
        url="${service.user.url}")
public interface UserClient {
    @GetMapping("/user/interest")
    String getUserInterest(@RequestHeader("X-USER-ID") String userId);
}
