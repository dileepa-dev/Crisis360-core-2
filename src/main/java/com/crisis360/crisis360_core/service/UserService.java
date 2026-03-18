package com.crisis360.crisis360_core.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.TopicManagementResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    public void saveTokenToDb(String token, String province, String district)
            throws ExecutionException, InterruptedException {

        token = normalize(token);
        province = normalize(province);
        district = normalize(district);

        if (token == null) {
            throw new IllegalArgumentException("Token is required");
        }
        if (province == null) {
            throw new IllegalArgumentException("Province is required");
        }
        if (district == null) {
            throw new IllegalArgumentException("District is required");
        }

        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("province", province);
        data.put("district", district);
        data.put("updatedAt", LocalDateTime.now().toString());

        db.collection("user_tokens").document(token).set(data).get();
    }

    private String normalize(String value) {
        if (value == null) return null;

        String cleaned = value.trim();

        if (cleaned.isEmpty()) return null;
        if ("null".equalsIgnoreCase(cleaned)) return null;
        if ("undefined".equalsIgnoreCase(cleaned)) return null;

        return cleaned;
    }
}
