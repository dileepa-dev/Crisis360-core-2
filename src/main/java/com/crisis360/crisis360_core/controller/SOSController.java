package com.crisis360.crisis360_core.controller;

import com.crisis360.crisis360_core.data.SOSRequest;
import com.crisis360.crisis360_core.service.SOSService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sos")
@CrossOrigin
public class SOSController {
    private final SOSService service;

    public SOSController(SOSService service) {
        this.service = service;
    }

    @PostMapping
    public String sendSOS(@RequestBody SOSRequest request) throws Exception {

        service.createSOS(
                request.getUserId(),
                request.getLatitude(),
                request.getLongitude()
        );

        return "SOS Sent Successfully";
    }
}
