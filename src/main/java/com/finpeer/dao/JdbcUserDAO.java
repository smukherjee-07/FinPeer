package com.finpeer.dao;

import com.finpeer.model.User;
import com.finpeer.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDAO implements UserDAO {
    private static final String COLUMNS = "user_id, name, email, password_hash, role";

    @Override public User save(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName()); statement.setString(2, user.getEmail()); statement.setString(3, user.getPasswordHash()); statement.setString(4, user.getRole());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { if (keys.next()) user.setUserId(keys.getInt(1)); }
            if (!"ADMIN".equals(user.getRole())) {
                String table = "BORROWER".equals(user.getRole()) ? "borrower_profiles" : "lender_profiles";
                try (PreparedStatement profile = connection.prepareStatement("INSERT INTO " + table + " (user_id) VALUES (?)")) { profile.setInt(1, user.getUserId()); profile.executeUpdate(); }
            }
            return user;
        } catch (SQLException exception) { throw new IllegalStateException("Could not save user", exception); }
    }
    @Override public Optional<User> findById(int userId) { return findOne("user_id = ?", userId); }
    @Override public Optional<User> findByEmail(String email) { return findOne("email = ?", email); }
    private Optional<User> findOne(String condition, Object value) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT " + COLUMNS + " FROM users WHERE " + condition)) {
            statement.setObject(1, value); try (ResultSet result = statement.executeQuery()) { return result.next() ? Optional.of(map(result)) : Optional.empty(); }
        } catch (SQLException exception) { throw new IllegalStateException("Could not read user", exception); }
    }
    @Override public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT " + COLUMNS + " FROM users ORDER BY user_id"); ResultSet result = statement.executeQuery()) {
            while (result.next()) users.add(map(result)); return users;
        } catch (SQLException exception) { throw new IllegalStateException("Could not list users", exception); }
    }
    @Override public boolean update(User user) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET name = ?, email = ?, password_hash = ?, role = ? WHERE user_id = ?")) {
            statement.setString(1, user.getName()); statement.setString(2, user.getEmail()); statement.setString(3, user.getPasswordHash()); statement.setString(4, user.getRole()); statement.setInt(5, user.getUserId()); return statement.executeUpdate() == 1;
        } catch (SQLException exception) { throw new IllegalStateException("Could not update user", exception); }
    }
    @Override public boolean delete(int userId) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) { statement.setInt(1, userId); return statement.executeUpdate() == 1; }
        catch (SQLException exception) { throw new IllegalStateException("Could not delete user", exception); }
    }
    private User map(ResultSet result) throws SQLException { return new User(result.getInt("user_id"), result.getString("name"), result.getString("email"), result.getString("password_hash"), result.getString("role")); }
}