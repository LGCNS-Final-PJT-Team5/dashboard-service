package com.modive.dashboard.service;

import com.modive.dashboard.client.LLMClient;
import com.modive.dashboard.dto.DriveFeedbacksDto;
import com.modive.dashboard.dto.SingleDriveFeedbackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class LLMServiceImpl implements LLMService{
    @Autowired
    private LLMClient llmClient;

    @Async
    public CompletableFuture<DriveFeedbacksDto> requestFeedbackAsync(SingleDriveFeedbackRequest request) {
        return CompletableFuture.completedFuture(llmClient.getDriveFeedbacksAsync(request));
    }
}
