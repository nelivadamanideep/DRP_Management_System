package com.erpms.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

class JwtServiceTest {

    private final JwtService service = new JwtService(
            "test-secret-must-be-at-least-thirty-two-characters-long!",
            Duration.ofHours(1),
            Duration.ofDays(7)
    );

    @Test
    void generatedAccessTokenIsValidAndCarriesClaims() {
        String token = service.generateAccessToken("jane@example.com", "SCIENTIST", "u-1");

        assertTrue(service.isTokenValid(token));
        assertTrue(service.isAccessToken(token));
        assertFalse(service.isRefreshToken(token));
        assertEquals("jane@example.com", service.extractEmail(token));
        assertEquals("SCIENTIST",         service.extractRole(token));
        assertEquals("u-1",               service.extractUserId(token));
    }

    @Test
    void refreshTokenIsIdentifiedAsRefresh() {
        String refresh = service.generateRefreshToken("jane@example.com", "u-1");

        assertTrue(service.isTokenValid(refresh));
        assertTrue(service.isRefreshToken(refresh));
        assertFalse(service.isAccessToken(refresh));
    }

    @Test
    void invalidTokenIsRejected() {
        assertFalse(service.isTokenValid("not-a-jwt"));
    }
}
