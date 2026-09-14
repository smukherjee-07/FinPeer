package com.finpeer.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class InputValidatorTest {
    @Test
    void acceptsNormalEmail() { assertTrue(InputValidator.isValidEmail("user@example.com")); }

    @Test
    void rejectsMalformedEmail() {
        assertFalse(InputValidator.isValidEmail(null));
        assertFalse(InputValidator.isValidEmail("userexample.com"));
        assertFalse(InputValidator.isValidEmail("user @example.com"));
    }

    @Test
    void checksPositiveAmounts() {
        assertTrue(InputValidator.isPositive(0.01));
        assertFalse(InputValidator.isPositive(0));
        assertFalse(InputValidator.isPositive(-1));
    }
}
