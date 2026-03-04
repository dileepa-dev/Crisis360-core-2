package com.crisis360.crisis360_core.service;

import com.crisis360.crisis360_core.enums.RiskLevel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SOSService {

    private static final String COLLECTION = "sos_alerts";

    public void createOrUpdateSOS(String userId, double latitude, double longitude) throws Exception {

        Firestore firestoreDb = FirestoreClient.getFirestore();

        Map<String, Object> sos = new HashMap<>();
        sos.put("userId", userId);
        sos.put("latitude", latitude);
        sos.put("longitude", longitude);
        sos.put("status", "ACTIVE");
        sos.put("riskLevel", RiskLevel.HIGH.name());
        sos.put("timestamp", LocalDateTime.now().toString());

        // ✅ Use userId as document ID
        DocumentReference docRef =
                firestoreDb.collection(COLLECTION).document(userId);

        // ✅ This will create if not exists OR update if exists
        ApiFuture<WriteResult> future =
                docRef.set(sos, SetOptions.merge());

        future.get();

        System.out.println("SOS created/updated for user: " + userId);
    }
}