package com.modive.dashboard.client;

import com.modive.dashboard.dto.RewardDto;
import com.modive.dashboard.dto.UserInfoResponse;
import com.modive.dashboard.dto.UserTypeResponse;
import com.modive.dashboard.enums.UserType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="user-service",
        url="${service.user.url}")
public interface UserClient {
    @GetMapping("/user/interest")
    UserTypeResponse getUserInterest(@RequestHeader("X-USER-ID") String userId);

    @GetMapping("/user")
    UserInfoResponse getUser(@RequestParam("userId") String userId);
}
