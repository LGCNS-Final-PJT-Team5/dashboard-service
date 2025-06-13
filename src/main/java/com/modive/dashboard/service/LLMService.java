package com.modive.dashboard.service;

import com.modive.dashboard.dto.DriveFeedbacksDto;
import com.modive.dashboard.dto.SingleDriveFeedbackRequest;

import java.util.concurrent.CompletableFuture;

public interface LLMService {
    CompletableFuture<DriveFeedbacksDto> requestFeedbackAsync(SingleDriveFeedbackRequest request);
}
