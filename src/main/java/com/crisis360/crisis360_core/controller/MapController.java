package com.crisis360.crisis360_core.controller;

import com.crisis360.crisis360_core.data.SafetyMapPoint;
import com.crisis360.crisis360_core.service.SafetyMapService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/map")
@CrossOrigin
public class MapController {

    private final SafetyMapService safetyMapService;

    public MapController(SafetyMapService safetyMapService) {
        this.safetyMapService = safetyMapService;
    }

    @GetMapping("/safety-points")
    public List<SafetyMapPoint> getSafetyPoints() throws Exception {
        return safetyMapService.getAllSafetyPoints();
    }
}