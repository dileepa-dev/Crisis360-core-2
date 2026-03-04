package com.crisis360.crisis360_core.controller;

import com.crisis360.crisis360_core.data.SafetySubmitRequest;
import com.crisis360.crisis360_core.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/safety")
@CrossOrigin
public class SafetyController {
    private final NotificationService notificationService;

    public SafetyController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<?> sendSafety(@RequestBody Map<String, String> request)
            throws Exception {

        String province = request.get("province");
        String district = request.get("district");

        notificationService.sendSafetyToDistrict(province, district);

        return ResponseEntity.ok("Notification Sent");
    }
    @PostMapping("/send-safety-confirmation")
    public ResponseEntity<?> safetyConfirmation(@RequestBody Map<String, String> request)
            throws Exception {
        String province = request.get("province");
        String district = request.get("district");
        String notificationId = request.get("notificationId");
        String message = request.get("message");
        String isSafe = request.get("isSafe");
        String peopleCount = request.get("peopleCount");
        String needHelp = request.get("needHelp");
        String severityLevel = request.get("severityLevel");
        String latitude = request.get("latitude");
        String longitude = request.get("longitude");



        return ResponseEntity.ok("Safety Confirmation Sent");
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody SafetySubmitRequest req) throws Exception {
        String id = notificationService.saveOrUpdateSafetyResponse(req);
        return ResponseEntity.ok(Map.of("status", "OK", "id", id));
    }
}
