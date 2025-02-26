package com.company.project.repository;

import com.company.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Repository for User CRUD operations using JdbcTemplate.
 */
@Repository
public class UserRepository {

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(UUID.fromString(rs.getString("user_id")));
            String managerIdStr = rs.getString("manager_id");
            if (managerIdStr != null) {
                user.setManagerId(UUID.fromString(managerIdStr));
            }
            user.setFullName(rs.getString("full_name"));
            user.setMobNum(rs.getString("mob_num"));
            user.setPanNum(rs.getString("pan_num"));
            user.setCreatedAt(rs.getTimestamp("created_at"));
            user.setUpdatedAt(rs.getTimestamp("updated_at"));
            user.setIsActive(rs.getBoolean("is_active"));
            return user;
        }
    };
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int createUser(User user) {
        String sql = "INSERT INTO users (user_id, manager_id, full_name, mob_num, pan_num, created_at, updated_at, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                user.getUserId().toString(),
                user.getManagerId() != null ? user.getManagerId().toString() : null,
                user.getFullName(),
                user.getMobNum(),
                user.getPanNum(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isIsActive()
        );
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public User getUserById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND is_active = true";
        List<User> users = jdbcTemplate.query(sql, new Object[]{userId}, userRowMapper);
        return users.isEmpty() ? null : users.get(0);
    }

    public User getUserByMob(String mobNum) {
        String sql = "SELECT * FROM users WHERE mob_num = ? AND is_active = true";
        List<User> users = jdbcTemplate.query(sql, new Object[]{mobNum}, userRowMapper);
        return users.isEmpty() ? null : users.get(0);
    }

    public List<User> getUsersByManagerId(String managerId) {
        String sql = "SELECT * FROM users WHERE manager_id = ? AND is_active = true";
        return jdbcTemplate.query(sql, new Object[]{managerId}, userRowMapper);
    }

    public int deleteUserById(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    public int deleteUserByMob(String mobNum) {
        String sql = "DELETE FROM users WHERE mob_num = ?";
        return jdbcTemplate.update(sql, mobNum);
    }

    public int updateUser(String userId, User user) {
        String sql = "UPDATE users SET full_name = ?, mob_num = ?, pan_num = ?, manager_id = ?, updated_at = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql,
                user.getFullName(),
                user.getMobNum(),
                user.getPanNum(),
                user.getManagerId() != null ? user.getManagerId().toString() : null,
                user.getUpdatedAt(),
                userId
        );
    }

    public int deactivateUser(String userId) {
        String sql = "UPDATE users SET is_active = false WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }
}
