package com.crisis360.crisis360_core.service;

import com.crisis360.crisis360_core.component.MlRiskClient;
import com.crisis360.crisis360_core.data.SafetySubmitRequest;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.messaging.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private static final String COLLECTION_NAME = "notifications";
    private static final String RESPONSES_COLLECTION = "safety_responses";
    private final MlRiskClient mlRiskClient;

    public NotificationService(MlRiskClient mlRiskClient) {
        this.mlRiskClient = mlRiskClient;
    }

    public void sendSafetyToDistrict(String province, String district) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection("user_tokens")
                .whereEqualTo("province", province)
                .whereEqualTo("district", district)
                .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        List<String> tokens = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            String token = doc.getString("token");
            if (token != null && !token.isBlank()) {
                tokens.add(token);
            }
        }

        if (tokens.isEmpty()) {
            System.out.println("No tokens found for " + province + " / " + district);
            return;
        }

        for (String token : tokens) {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle("Safety Confirmation Required")
                            .setBody("Please confirm your safety status")
                            .build())
                    .putData("type", "SAFETY_CHECK")
                    .putData("province", province)
                    .putData("district", district)
                    .putData("timestamp", LocalDateTime.now().toString())
                    .build();

            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Sent to token: " + token + " -> " + response);
            } catch (Exception e) {
                System.out.println("Failed token: " + token + " error: " + e.getMessage());
            }
        }
    }

    public String saveOrUpdateSafetyResponse(SafetySubmitRequest req) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        if (req.getUserId() == null || req.getUserId().isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (req.getNotificationId() == null || req.getNotificationId().isBlank()) {
            throw new IllegalArgumentException("notificationId is required");
        }

        String message = req.getMessage() == null ? "" : req.getMessage().trim();
        String riskLevel = mlRiskClient.predictRiskLevel(message); // LOW/MEDIUM/HIGH/UNKNOWN

        Map<String, Object> data = new HashMap<>();
        data.put("notificationId", req.getNotificationId()); // last notification responded to
        data.put("district", req.getDistrict());
        data.put("message", req.getMessage());
        data.put("isSafe", req.getIsSafe());
        data.put("peopleCount", req.getPeopleCount());
        data.put("needHelp", req.getNeedHelp());
        data.put("severityLevel", req.getSeverityLevel());
        data.put("latitude", req.getLatitude());
        data.put("longitude", req.getLongitude());
        data.put("userId", req.getUserId());
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("riskLevel", riskLevel);
        data.put("riskScore", computeRiskScore(req));

        // ✅ ONLY ONE DOC PER USER
        String docId = req.getUserId();
        DocumentReference ref = db.collection(RESPONSES_COLLECTION).document(docId);

        ref.set(data, SetOptions.merge()).get();
        return docId;
    }

    private double computeRiskScore(SafetySubmitRequest req) {
        double score = req.getSeverityLevel() == null ? 0 : req.getSeverityLevel();
        if ("NO".equalsIgnoreCase(req.getIsSafe())) score += 2.0;
        if ("YES".equalsIgnoreCase(req.getNeedHelp())) score += 2.0;
        if (score < 1) score = 1;
        if (score > 10) score = 10;
        return score;
    }
}
