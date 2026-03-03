package com.crisis360.crisis360_core.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.TopicManagementResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {

    public void subscribeUserToDistrictTopic(String token,
                                             String province,
                                             String district) throws Exception {

        String topicName = province + "_" + district;

        TopicManagementResponse response =
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(
                                Collections.singletonList(token),
                                topicName
                        );

        System.out.println("Subscribed to topic: " + topicName);
    }
}
