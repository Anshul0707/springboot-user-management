package com.company.project.controller;


import com.company.project.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller exposing user endpoints.
 */
@RestController
@Api(value = "User Management System", tags = "User API")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Create a new user")
    @PostMapping("/create_user")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = userService.createUser(payload);
        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Retrieve user(s)")
    @PostMapping("/get_users")
    public ResponseEntity<?> getUsers(@RequestBody(required = false) Map<String, Object> payload) {
        if (payload == null) {
            payload = new java.util.HashMap<>();
        }
        Map<String, Object> response = userService.getUsers(payload);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Delete a user")
    @PostMapping("/delete_user")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = userService.deleteUser(payload);
        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Update user(s)")
    @PostMapping("/update_user")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = userService.updateUser(payload);
        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
