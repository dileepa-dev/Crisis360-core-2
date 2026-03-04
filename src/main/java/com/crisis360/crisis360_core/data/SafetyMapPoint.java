package com.crisis360.crisis360_core.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SafetyMapPoint {
    private String id;
    private String userId;
    private String district;
    private String riskLevel;
    private Double latitude;
    private Double longitude;
    private String timestamp;
}