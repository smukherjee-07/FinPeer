package com.finpeer.service;

import static org.junit.jupiter.api.Assertions.*;
import com.finpeer.dao.UserDAO;
import com.finpeer.model.User;
import com.finpeer.exception.AuthenticationException;
import java.util.*;
import org.junit.jupiter.api.Test;

class AuthServiceTest {
    @Test
    void registersHashedPasswordAndNormalizedFields() {
        InMemoryUserDAO dao = new InMemoryUserDAO();
        User user = new AuthService(dao).register(" Ada ", "ADA@EXAMPLE.COM", "password1", "borrower");
        assertEquals("Ada", user.getName());
        assertEquals("ada@example.com", user.getEmail());
        assertEquals("BORROWER", user.getRole());
        assertNotEquals("password1", user.getPasswordHash());
    }

    @Test
    void logsInWithCorrectPassword() throws AuthenticationException {
        InMemoryUserDAO dao = new InMemoryUserDAO();
        new AuthService(dao).register("Ada", "ada@example.com", "password1", "LENDER");
        User loggedIn = new AuthService(dao).login("ada@example.com", "password1");
        assertEquals("Ada", loggedIn.getName());
    }

    @Test
    void rejectsWrongPasswordAndDuplicateEmail() {
        InMemoryUserDAO dao = new InMemoryUserDAO();
        AuthService service = new AuthService(dao);
        service.register("Ada", "ada@example.com", "password1", "LENDER");
        assertThrows(AuthenticationException.class, () -> service.login("ada@example.com", "wrongpass"));
        assertThrows(IllegalArgumentException.class, () -> service.register("Other", "ada@example.com", "password2", "LENDER"));
    }

    private static class InMemoryUserDAO implements UserDAO {
        private final Map<Integer, User> users = new HashMap<>();
        private int nextId = 1;
        public User save(User user) { user.setUserId(nextId++); users.put(user.getUserId(), user); return user; }
        public Optional<User> findById(int id) { return Optional.ofNullable(users.get(id)); }
        public Optional<User> findByEmail(String email) { return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst(); }
        public List<User> findAll() { return new ArrayList<>(users.values()); }
        public boolean update(User user) { return users.replace(user.getUserId(), user) != null; }
        public boolean delete(int id) { return users.remove(id) != null; }
    }
}
