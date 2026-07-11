package com.erpms.common.security;

/**
 * Value object stored as the Spring Security principal by
 * {@link com.erpms.security.JwtAuthenticationFilter}.
 *
 * <p>Carrying the userId next to the email avoids repeated database
 * lookups in downstream services and controllers.
 */
public record AuthenticatedUser(
        String userId,
        String email,
        String role
) {}
