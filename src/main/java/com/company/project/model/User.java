package com.company.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Model class representing a User.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID userId;
    private UUID managerId;
    private String fullName;
    private String mobNum;
    private String panNum;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isActive;

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
