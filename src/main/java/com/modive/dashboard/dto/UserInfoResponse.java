package com.modive.dashboard.dto;

import com.modive.dashboard.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserInfoResponse {
    public UserInfo data;
    public int code;
    public String message;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        public String userId;
        public String nickname;
        public String email;
        public int experience;
        public String joinedAt;
        public int seedBalance;
        public int isActive;
    }
}