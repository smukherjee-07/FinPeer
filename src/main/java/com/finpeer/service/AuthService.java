package com.finpeer.service;

import com.finpeer.exception.AuthenticationException;
import com.finpeer.model.User;
import com.finpeer.dao.JdbcUserDAO;
import com.finpeer.dao.UserDAO;
import com.finpeer.util.InputValidator;
import com.finpeer.util.PasswordUtil;

/** Business logic for registration, login, and password handling. */
public class AuthService {
    private final UserDAO userDAO;

    public AuthService() { this(new JdbcUserDAO()); }
    public AuthService(UserDAO userDAO) { this.userDAO = userDAO; }

    public User login(String email, String password) throws AuthenticationException {
        if (!InputValidator.isValidEmail(email)) throw new AuthenticationException("Enter a valid email address, for example abc123@text.com.");
        if (password == null || password.isBlank() || password.length() < 8) throw new AuthenticationException("Enter your password. Passwords must contain at least 8 characters.");
        User user = userDAO.findByEmail(email.trim().toLowerCase()).orElseThrow(() -> new AuthenticationException("No account was found for this email. Register first, then log in."));
        if (!PasswordUtil.verify(password, user.getPasswordHash())) throw new AuthenticationException("The password is incorrect. Check it and try again.");
        return user;
    }

    public User register(String name, String email, String password, String role) {
        if (name == null || name.isBlank() || !InputValidator.isValidEmail(email) || password == null || password.length() < 8) throw new IllegalArgumentException("Name, valid email, and an 8-character password are required");
        String normalizedRole = role == null ? "" : role.trim().toUpperCase();
        if (!normalizedRole.equals("BORROWER") && !normalizedRole.equals("LENDER") && !normalizedRole.equals("ADMIN")) throw new IllegalArgumentException("Unsupported role");
        if (userDAO.findByEmail(email).isPresent()) throw new IllegalArgumentException("Email is already registered");
        return userDAO.save(new User(0, name.trim(), email.trim().toLowerCase(), PasswordUtil.hash(password), normalizedRole));
    }
}
