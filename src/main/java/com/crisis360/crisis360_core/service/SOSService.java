package com.crisis360.crisis360_core.service;

import com.crisis360.crisis360_core.enums.RiskLevel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
import com.crisis360.crisis360_core.data.SosMapPoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SOSService {

    private static final String SOS_COLLECTION = "sos_alerts";

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
                firestoreDb.collection(SOS_COLLECTION).document(userId);

        // ✅ This will create if not exists OR update if exists
        ApiFuture<WriteResult> future =
                docRef.set(sos, SetOptions.merge());

        future.get();

        System.out.println("SOS created/updated for user: " + userId);
    }

    public List<SosMapPoint> getAllSosPoints() throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection(SOS_COLLECTION).get();
        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        List<SosMapPoint> result = new ArrayList<>();

        for (QueryDocumentSnapshot doc : docs) {
            Map<String, Object> d = doc.getData();

            Double lat = toDouble(d.get("latitude"));
            Double lng = toDouble(d.get("longitude"));
            if (lat == null || lng == null) continue;

            SosMapPoint p = new SosMapPoint();
            p.setId(doc.getId());
            p.setUserId(String.valueOf(d.getOrDefault("userId", "")));
            p.setRiskLevel(String.valueOf(d.getOrDefault("riskLevel", "UNKNOWN")));
            p.setStatus(String.valueOf(d.getOrDefault("status", "UNKNOWN")));
            p.setLatitude(lat);
            p.setLongitude(lng);
            p.setTimestamp(String.valueOf(d.getOrDefault("timestamp", "")));

            result.add(p);
        }

        return result;
    }

    private Double toDouble(Object v) {
        if (v == null) return null;
        if (v instanceof Double) return (Double) v;
        if (v instanceof Long) return ((Long) v).doubleValue();
        if (v instanceof Integer) return ((Integer) v).doubleValue();
        if (v instanceof String) {
            try { return Double.parseDouble((String) v); } catch (Exception ignored) {}
        }
        return null;
    }
}