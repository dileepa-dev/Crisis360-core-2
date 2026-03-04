package com.crisis360.crisis360_core.service;

import com.crisis360.crisis360_core.data.SafetyMapPoint;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SafetyMapService {

    private static final String RESPONSES_COLLECTION = "safety_responses"; // <- your collection name

    public List<SafetyMapPoint> getAllSafetyPoints() throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future =
                db.collection(RESPONSES_COLLECTION).get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        List<SafetyMapPoint> result = new ArrayList<>();

        for (QueryDocumentSnapshot doc : docs) {
            Map<String, Object> d = doc.getData();

            Double lat = toDouble(d.get("latitude"));
            Double lng = toDouble(d.get("longitude"));
            if (lat == null || lng == null) continue;

            SafetyMapPoint p = new SafetyMapPoint();
            p.setId(doc.getId());
            p.setUserId(String.valueOf(d.getOrDefault("userId", "")));
            p.setRiskLevel(String.valueOf(d.getOrDefault("riskLevel", "UNKNOWN")));
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