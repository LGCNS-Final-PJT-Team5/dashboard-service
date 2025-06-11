package com.modive.dashboard.dto;

import com.modive.dashboard.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserTypeResponse {
    public String data;
    public int code;
    public String message;
}
