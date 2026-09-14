package com.finpeer.util;

/** Shared validation helpers for CLI input (email format, positive amounts, etc.) */
public class InputValidator {
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    public static boolean isPositive(double value) {
        return value > 0;
    }
}
