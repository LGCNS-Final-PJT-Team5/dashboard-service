package com.modive.dashboard.dto.admin;

import lombok.Data;

import java.util.Map;

@Data
public class AdminResponse<T> {

    private int status;
    private String message;
    private Map<String, T> data;

    public AdminResponse(int status, String message, Map<String,T> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
