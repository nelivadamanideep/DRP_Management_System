package com.erpms.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Convenience accessor for the currently authenticated principal.
 *
 * <p>The {@link com.erpms.security.JwtAuthenticationFilter} stores the
 * {@link AuthenticatedUser} value object as the principal, so callers
 * anywhere in the service layer can resolve the caller without wiring
 * {@code HttpServletRequest}.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * @return the current authenticated user, or {@code null} when the
     *         request is anonymous (e.g. public endpoints).
     */
    public static AuthenticatedUser currentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthenticatedUser au) {
            return au;
        }
        return null;
    }

    public static String currentUserIdOrNull() {
        AuthenticatedUser u = currentUserOrNull();
        return u == null ? null : u.userId();
    }

    public static String currentEmailOrNull() {
        AuthenticatedUser u = currentUserOrNull();
        return u == null ? null : u.email();
    }
}
