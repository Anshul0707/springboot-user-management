package com.company.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Model class representing a Manager.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
    private UUID managerId;
    private String fullName;
    private String email;
    private boolean isActive;
    private Timestamp createdAt;

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
