package com.modive.dashboard.enums;

public enum UserType {
    ECO("연비"),
    INSURANCE("보험료"),
    BEGINNER("초보운전"),
    REWARD("앱테크"),
    MAINTENANCE("차량관리"),
    CARBON("탄소절감"),
    DRIVESTAR("드라이브스타"),
    TECHNIQUE("드라이빙테크닉");

    private final String label;

    UserType(String label) {
        this.label = label;
    }

    public static UserType fromLabel(String label) {
        for (UserType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }

    public String getLabel() {
        return label;
    }
}

