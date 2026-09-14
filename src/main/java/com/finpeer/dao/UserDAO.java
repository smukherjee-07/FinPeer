package com.finpeer.dao;

import com.finpeer.model.User;
import java.util.List;
import java.util.Optional;

/** Data access layer for User (and subtype) records. Raw JDBC/SQL lives here only. */
public interface UserDAO {
    User save(User user);
    Optional<User> findById(int userId);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    boolean update(User user);
    boolean delete(int userId);
}
