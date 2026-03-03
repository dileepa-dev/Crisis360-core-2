package com.crisis360.crisis360_core.service;

import com.google.firebase.messaging.*;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private static final String COLLECTION_NAME = "notifications";

    public void sendSafetyToDistrict(String province, String district) throws Exception {
        String topicName = sanitizeTopic(province + "_" + district);

        // Prepare notification data
        Map<String, Object> data = new HashMap<>();
        data.put("type", "SAFETY_CHECK");
        data.put("province", province);
        data.put("district", district);
        data.put("timestamp", LocalDateTime.now().toString());

        // Save notification to Firestore
        saveNotificationToDB(topicName, data);

        // Send FCM notification
        Message message = Message.builder()
                .setTopic(topicName)
                .setNotification(Notification.builder()
                        .setTitle("Safety Confirmation Required")
                        .setBody("Please confirm your safety status")
                        .build())
                .putAllData((Map<String, String>) (Map) data)
                .build();

        FirebaseMessaging.getInstance().send(message);
        System.out.println("Notification sent to topic: " + topicName);
    }

    private void saveNotificationToDB(String topic, Map<String, Object> data) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> docData = new HashMap<>(data);
        docData.put("topic", topic); // sanitized topic (for FCM)
        docData.put("district", data.get("district")); // store district explicitly
        db.collection(COLLECTION_NAME).add(docData).get();
        System.out.println("Notification saved to DB for topic: " + topic);
    }


    private String sanitizeTopic(String input) {
        // Replace all spaces with underscores, remove invalid characters
        return input.trim().replaceAll("[^a-zA-Z0-9-_.~%]", "_");
    }

}
