package com.crisis360.crisis360_core.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeepAliveService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String HF_URL = "https://dileepamalshan-crisis360.hf.space/";

    @Scheduled(fixedRate = 8 * 60 * 1000)
//    @Scheduled(fixedRate = 10000)
    public void keepHfSpaceAlive() {
        try {
            var resp = restTemplate.getForEntity(HF_URL, String.class);
            System.out.println("[KeepAlive] HF ping status=" + resp.getStatusCode());
        } catch (Exception e) {
            System.out.println("[KeepAlive] Failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}