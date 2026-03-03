package com.crisis360.crisis360_core.controller;

import com.crisis360.crisis360_core.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/save-token")
    public String saveToken(@RequestBody Map<String, String> request)
            throws Exception {

        String token = request.get("token");
        String province = request.get("province");
        String district = request.get("district");

        userService.subscribeUserToDistrictTopic(token, province, district);

        return "Token saved and subscribed";
    }
}

