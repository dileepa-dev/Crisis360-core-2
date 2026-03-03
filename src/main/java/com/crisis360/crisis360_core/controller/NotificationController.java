package com.crisis360.crisis360_core.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private static final String COLLECTION_NAME = "notifications";

    @GetMapping("/{district}")
    public List<Map<String, Object>> getNotificationsByDistrict(@PathVariable String district) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference notifications = db.collection(COLLECTION_NAME);

        // Query only notifications for this district
        ApiFuture<QuerySnapshot> query = notifications.whereEqualTo("district", district).get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();

        List<Map<String, Object>> result = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            result.add(doc.getData());
        }
        return result;
    }
}
