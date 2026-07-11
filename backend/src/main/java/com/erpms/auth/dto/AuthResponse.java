package com.erpms.auth.dto;

/**
 * Payload returned to the client after a successful login, registration or refresh.
 *
 * <p>The client must store {@code accessToken} in memory and {@code refreshToken}
 * in a secure store; the frontend rotates them via <code>POST /auth/refresh</code>.
 */
public record AuthResponse(
        String userId,
        String email,
        String fullName,
        String role,
        String accessToken,
        String refreshToken,
        long accessTokenExpiresInSeconds
) {}
