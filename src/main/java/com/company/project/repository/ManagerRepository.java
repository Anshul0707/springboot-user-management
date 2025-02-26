package com.company.project.repository;


import com.company.project.model.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository for Manager operations.
 */
@Repository
public class ManagerRepository {

    private final RowMapper<Manager> managerRowMapper = new RowMapper<Manager>() {
        @Override
        public Manager mapRow(ResultSet rs, int rowNum) throws SQLException {
            Manager manager = new Manager();
            manager.setManagerId(java.util.UUID.fromString(rs.getString("manager_id")));
            manager.setFullName(rs.getString("full_name"));
            manager.setEmail(rs.getString("email"));
            manager.setIsActive(rs.getBoolean("is_active"));
            manager.setCreatedAt(rs.getTimestamp("created_at"));
            return manager;
        }
    };
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Manager getManagerById(String managerId) {
        String sql = "SELECT * FROM managers WHERE manager_id = ? AND is_active = true";
        List<Manager> managers = jdbcTemplate.query(sql, new Object[]{managerId}, managerRowMapper);
        return managers.isEmpty() ? null : managers.get(0);
    }
}
