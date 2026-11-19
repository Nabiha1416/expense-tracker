package com.nabiha.expense_tracker;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService =
            new JwtService("this-is-a-test-secret-key-with-at-least-32-characters");

    @Test
    void generatedTokenContainsUsername() {
        String token = jwtService.generateToken("nabiha");
        assertEquals("nabiha", jwtService.extractUsername(token));
    }

    @Test
    void tokenSignedWithDifferentSecretIsRejected() {
        JwtService other = new JwtService("a-completely-different-secret-key-with-32-plus-characters");
        String token = other.generateToken("nabiha");
        assertThrows(JwtException.class, () -> jwtService.extractUsername(token));
    }
}
