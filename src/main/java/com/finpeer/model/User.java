package com.finpeer.model;

/**
 * Base entity representing any account holder in FinPeer.
 * Borrower and Lender extend this with role-specific fields.
 */
public class User {
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private String role; // ADMIN, BORROWER, LENDER

    public User() {}

    public User(int userId, String name, String email, String passwordHash, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
