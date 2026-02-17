package com.crisis360.crisis360_core.data;

import lombok.Data;

@Data
public class SOSRequest {
    private String userId;
    private double latitude;
    private double longitude;
}

