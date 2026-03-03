package com.crisis360.crisis360_core.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SafetyConfirmation {
    private String id;
    private String province;
    private String district;
    private String type;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
}
