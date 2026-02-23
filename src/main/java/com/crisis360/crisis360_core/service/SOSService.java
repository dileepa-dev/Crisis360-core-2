package com.crisis360.crisis360_core.service;

import com.crisis360.crisis360_core.enums.RiskLevel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SOSService {
    public void createSOS(String userId, double latitude, double longitude) throws Exception {

        Firestore firestoreDb = FirestoreClient.getFirestore();

        Map<String, Object> sos = new HashMap<>();
        sos.put("userId", userId);
        sos.put("latitude", latitude);
        sos.put("longitude", longitude);
        sos.put("status", "ACTIVE");
        sos.put("riskLevel", RiskLevel.HIGH.name()); // safer
        sos.put("timestamp", LocalDateTime.now().toString());

        ApiFuture<DocumentReference> future =
                firestoreDb.collection("sos_alerts").add(sos);

        // 🔥 WAIT FOR FIRESTORE WRITE
        DocumentReference documentReference = future.get();

        System.out.println("Saved SOS with ID: " + documentReference.getId());
    }
}
