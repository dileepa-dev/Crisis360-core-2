package com.crisis360.crisis360_core.controller;

import com.crisis360.crisis360_core.data.SafetySubmitRequest;
import com.crisis360.crisis360_core.service.NotificationService;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/safety")
@CrossOrigin
public class SafetyController {
    private final NotificationService notificationService;

    public SafetyController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send-safety-confirmation")
    public ResponseEntity<?> sendSafety(@RequestBody Map<String, String> request)
            throws Exception {

        String province = request.get("province");
        String district = request.get("district");

        String title = "Safety Confirmation Required";
        String message = "Please confirm your safety status";

        // 🔹 Save notification to Firestore
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("province", province);
        notificationData.put("district", district);
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("type", "SAFETY_CHECK");
        notificationData.put("timestamp", LocalDateTime.now().toString());

        db.collection("notifications").add(notificationData).get();

        // 🔹 Send push notification
        notificationService.sendSafetyToDistrict(province, district);

        return ResponseEntity.ok("Notification Sent Successfully");
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody SafetySubmitRequest req) throws Exception {
        String id = notificationService.saveOrUpdateSafetyResponse(req);
        return ResponseEntity.ok(Map.of("status", "OK", "id", id));
    }
}
