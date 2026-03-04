package com.crisis360.crisis360_core.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SosMapPoint {
    private String id;
    private String userId;
    private String riskLevel;
    private String status;
    private Double latitude;
    private Double longitude;
    private String timestamp;
}
