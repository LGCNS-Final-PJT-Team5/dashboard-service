package com.modive.dashboard.client;

import com.modive.dashboard.dto.DriveFeedbackRequest;
import com.modive.dashboard.dto.DriveFeedbacksDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "llm-service", url = "http://llm-service:8080")
public interface LLMClient {

    @PostMapping("/llm/post-feedbacks")
    DriveFeedbacksDto getDriveFeedbacks(@RequestBody DriveFeedbackRequest params);

}
