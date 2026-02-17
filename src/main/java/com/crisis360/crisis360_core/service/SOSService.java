package com.crisis360.crisis360_core.service;

import com.crisis360.crisis360_core.RiskLevel;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SOSService {
    public void createSOS (String userId, double latitude, double longitude){
        Firestore firestoreDb = FirestoreClient.getFirestore();

        Map<String, Object> sos = new HashMap<>();
        sos.put("userId", userId);
        sos.put("latitude", latitude);
        sos.put("longitude", longitude);
        sos.put("status", "ACTIVE");
        sos.put("riskLevel", RiskLevel.HIGH);
        sos.put("timestamp", LocalDateTime.now().toString());

        firestoreDb.collection("sos_alerts").add(sos);
    }
}
