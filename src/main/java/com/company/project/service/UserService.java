package com.company.project.service;


import com.company.project.model.Manager;
import com.company.project.model.User;
import com.company.project.repository.ManagerRepository;
import com.company.project.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service layer for user operations and validations.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ManagerRepository managerRepository;

    // Validate that full name is not empty
    public boolean validateFullName(String fullName) {
        return StringUtils.hasText(fullName);
    }

    // Validate and format mobile number (removes allowed prefixes "0" or "+91")
    public String validateAndFormatMobile(String mobNum) {
        if (!StringUtils.hasText(mobNum)) return null;
        if (mobNum.startsWith("+91")) {
            mobNum = mobNum.substring(3);
        } else if (mobNum.startsWith("0")) {
            mobNum = mobNum.substring(1);
        }
        mobNum = mobNum.replaceAll("[\\s-]", "");
        if (mobNum.matches("\\d{10}")) {
            return mobNum;
        }
        return null;
    }

    // Validate and format PAN number: pattern [A-Z]{5}[0-9]{4}[A-Z]{1}
    public String validateAndFormatPan(String panNum) {
        if (!StringUtils.hasText(panNum)) return null;
        panNum = panNum.toUpperCase();
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
        Matcher matcher = pattern.matcher(panNum);
        if (matcher.matches()) {
            return panNum;
        }
        return null;
    }

    // Validate that manager exists and is active
    public boolean validateManager(String managerId) {
        if (!StringUtils.hasText(managerId)) return false;
        Manager manager = managerRepository.getManagerById(managerId);
        return manager != null;
    }

    /**
     * Creates a new user after performing all necessary validations.
     */
    public Map<String, Object> createUser(Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        String fullName = (String) payload.get("full_name");
        String mobNum = (String) payload.get("mob_num");
        String panNum = (String) payload.get("pan_num");
        String managerId = payload.get("manager_id") != null ? payload.get("manager_id").toString() : null;

        // Validate input
        if (!validateFullName(fullName)) {
            response.put("error", "Full name must not be empty.");
            return response;
        }
        String formattedMob = validateAndFormatMobile(mobNum);
        if (formattedMob == null) {
            response.put("error", "Invalid mobile number. It must be a valid 10-digit number.");
            return response;
        }
        String formattedPan = validateAndFormatPan(panNum);
        if (formattedPan == null) {
            response.put("error", "Invalid PAN number. It must follow the format AABCP1234C.");
            return response;
        }
        if (managerId != null && !validateManager(managerId)) {
            response.put("error", "Invalid manager_id. Manager does not exist or is inactive.");
            return response;
        }

        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setFullName(fullName);
        user.setMobNum(formattedMob);
        user.setPanNum(formattedPan);
        if (managerId != null) {
            user.setManagerId(UUID.fromString(managerId));
        }
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(null);
        user.setIsActive(true);

        int result = userRepository.createUser(user);
        if (result > 0) {
            logger.info("User created with ID {}", user.getUserId());
            response.put("message", "User created successfully.");
        } else {
            response.put("error", "Failed to create user.");
        }
        return response;
    }

    /**
     * Retrieves users based on provided filters.
     */
    public Map<String, Object> getUsers(Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        List<User> users = new ArrayList<>();

        if (payload.containsKey("user_id")) {
            User user = userRepository.getUserById(payload.get("user_id").toString());
            if (user != null) users.add(user);
        } else if (payload.containsKey("mob_num")) {
            User user = userRepository.getUserByMob(payload.get("mob_num").toString());
            if (user != null) users.add(user);
        } else if (payload.containsKey("manager_id")) {
            users = userRepository.getUsersByManagerId(payload.get("manager_id").toString());
        } else {
            users = userRepository.getAllUsers();
        }

        response.put("users", users);
        return response;
    }

    /**
     * Deletes a user identified by user_id or mob_num.
     */
    public Map<String, Object> deleteUser(Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        if (payload.containsKey("user_id")) {
            User user = userRepository.getUserById(payload.get("user_id").toString());
            if (user == null) {
                response.put("error", "User with provided user_id not found.");
                return response;
            }
            userRepository.deleteUserById(payload.get("user_id").toString());
            response.put("message", "User deleted successfully.");
        } else if (payload.containsKey("mob_num")) {
            User user = userRepository.getUserByMob(payload.get("mob_num").toString());
            if (user == null) {
                response.put("error", "User with provided mobile number not found.");
                return response;
            }
            userRepository.deleteUserByMob(payload.get("mob_num").toString());
            response.put("message", "User deleted successfully.");
        } else {
            response.put("error", "Missing key: Provide either user_id or mob_num.");
        }
        return response;
    }

    /**
     * Updates one or more users based on provided user_ids and update_data.
     * For bulk updates (more than one user_id), only manager_id can be updated.
     * If manager_id is updated and the user already has a manager, the current record
     * is deactivated and a new record is inserted.
     */
    public Map<String, Object> updateUser(Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        if (!payload.containsKey("user_ids") || !payload.containsKey("update_data")) {
            response.put("error", "Missing keys: user_ids and update_data are required.");
            return response;
        }

        List<String> userIds = (List<String>) payload.get("user_ids");
        Map<String, Object> updateData = (Map<String, Object>) payload.get("update_data");

        // For bulk update, only manager_id is allowed.
        if (userIds.size() > 1) {
            for (String key : updateData.keySet()) {
                if (!key.equals("manager_id")) {
                    response.put("error", "Bulk update only supports manager_id. Extra key: " + key);
                    return response;
                }
            }
        }

        for (String userId : userIds) {
            User existingUser = userRepository.getUserById(userId);
            if (existingUser == null) {
                response.put("error", "User with user_id " + userId + " not found.");
                continue;
            }

            // Validate and assign new values or retain existing ones
            String fullName = updateData.containsKey("full_name") ? (String) updateData.get("full_name") : existingUser.getFullName();
            if (updateData.containsKey("full_name") && !validateFullName(fullName)) {
                response.put("error", "Full name must not be empty for user_id " + userId);
                continue;
            }
            String mobNum = updateData.containsKey("mob_num") ? (String) updateData.get("mob_num") : existingUser.getMobNum();
            if (updateData.containsKey("mob_num")) {
                String formattedMob = validateAndFormatMobile(mobNum);
                if (formattedMob == null) {
                    response.put("error", "Invalid mobile number for user_id " + userId);
                    continue;
                }
                mobNum = formattedMob;
            }
            String panNum = updateData.containsKey("pan_num") ? (String) updateData.get("pan_num") : existingUser.getPanNum();
            if (updateData.containsKey("pan_num")) {
                String formattedPan = validateAndFormatPan(panNum);
                if (formattedPan == null) {
                    response.put("error", "Invalid PAN number for user_id " + userId);
                    continue;
                }
                panNum = formattedPan;
            }

            // Handle manager update
            if (updateData.containsKey("manager_id")) {
                String newManagerId = updateData.get("manager_id").toString();
                if (!validateManager(newManagerId)) {
                    response.put("error", "Invalid manager_id for user_id " + userId);
                    continue;
                }

                // If no previous manager, update in place.
                if (existingUser.getManagerId() == null) {
                    existingUser.setManagerId(java.util.UUID.fromString(newManagerId));
                    existingUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    userRepository.updateUser(userId, existingUser);
                } else if (!existingUser.getManagerId().toString().equals(newManagerId)) {
                    // Deactivate current record and create a new record with new manager_id.
                    userRepository.deactivateUser(userId);
                    User newUser = new User();
                    newUser.setUserId(UUID.randomUUID());
                    newUser.setFullName(existingUser.getFullName());
                    newUser.setMobNum(existingUser.getMobNum());
                    newUser.setPanNum(existingUser.getPanNum());
                    newUser.setManagerId(UUID.fromString(newManagerId));
                    newUser.setCreatedAt(existingUser.getCreatedAt());
                    newUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    newUser.setIsActive(true);
                    userRepository.createUser(newUser);
                }
            } else {
                // For non-manager updates (or single record update)
                existingUser.setFullName(fullName);
                existingUser.setMobNum(mobNum);
                existingUser.setPanNum(panNum);
                existingUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                userRepository.updateUser(userId, existingUser);
            }
        }

        response.put("message", "User(s) updated successfully.");
        return response;
    }
}
