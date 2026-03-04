package com.crisis360.crisis360_core.controller;

import com.crisis360.crisis360_core.data.SOSRequest;
import com.crisis360.crisis360_core.data.SosMapPoint;
import com.crisis360.crisis360_core.service.SOSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sos")
@CrossOrigin
public class SOSController {
    private final SOSService service;

    public SOSController(SOSService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> sendSOS(@RequestBody SOSRequest request) {
        try {
            service.createOrUpdateSOS(
                    request.getUserId(),
                    request.getLatitude(),
                    request.getLongitude()
            );

            return ResponseEntity.ok("SOS Sent Successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to save SOS");
        }
    }

    @GetMapping("/sos-points")
    public List<SosMapPoint> getSosPoints() throws Exception {
        return service.getAllSosPoints();
    }
}
