package com.crisis360.crisis360_core.controller;

import com.crisis360.crisis360_core.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/save-token")
    public ResponseEntity<Map<String, Object>> saveToken(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = normalize(request.get("token"));
            String province = normalize(request.get("province"));
            String district = normalize(request.get("district"));

            if (token == null) {
                response.put("success", false);
                response.put("message", "Token is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (province == null) {
                response.put("success", false);
                response.put("message", "Province is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (district == null) {
                response.put("success", false);
                response.put("message", "District is required");
                return ResponseEntity.badRequest().body(response);
            }

            userService.saveTokenToDb(token, province, district);

            response.put("success", true);
            response.put("message", "Token saved successfully");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to save token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String normalize(String value) {
        if (value == null) return null;

        String cleaned = value.trim();

        if (cleaned.isEmpty()) return null;
        if ("null".equalsIgnoreCase(cleaned)) return null;
        if ("undefined".equalsIgnoreCase(cleaned)) return null;

        return cleaned;
    }
}

