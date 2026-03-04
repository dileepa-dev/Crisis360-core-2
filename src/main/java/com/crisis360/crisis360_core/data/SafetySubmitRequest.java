package com.crisis360.crisis360_core.data;

import com.google.firebase.database.annotations.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SafetySubmitRequest {
    @NotNull
    private String notificationId;
    @NotNull
    private String district;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotNull
    private String userId;
    private String message;
    private String isSafe;
    private String peopleCount;
    private String needHelp;
    private Double severityLevel;
}
