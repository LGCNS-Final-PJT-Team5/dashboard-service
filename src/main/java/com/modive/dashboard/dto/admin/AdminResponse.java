package com.modive.dashboard.dto.admin;

import lombok.Data;

@Data
public class AdminResponse<T> {

    private int status;
    private String message;
    private T data;

    public AdminResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
