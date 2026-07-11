package com.erpms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Signs and validates JWTs used for both access and refresh tokens.
 *
 * <p>Access tokens (default TTL 1h) carry {@code userId}, {@code role} and
 * {@code type=access}. Refresh tokens (default TTL 7d) carry only
 * {@code userId} and {@code type=refresh} and are additionally persisted
 * server-side so they can be revoked.
 */
@Service
public class JwtService {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_TYPE = "type";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final Duration accessTtl;
    private final Duration refreshTtl;

    public JwtService(
            @Value("${erpms.security.jwt-secret}") String secret,
            @Value("${erpms.security.access-token-ttl:PT1H}") Duration accessTtl,
            @Value("${erpms.security.refresh-token-ttl:P7D}") Duration refreshTtl
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtl = accessTtl;
        this.refreshTtl = refreshTtl;
    }

    public String generateAccessToken(String email, String role, String userId) {
        return build(email, Map.of(CLAIM_ROLE, role, CLAIM_USER_ID, userId, CLAIM_TYPE, TYPE_ACCESS), accessTtl);
    }

    public String generateRefreshToken(String email, String userId) {
        return build(email, Map.of(CLAIM_USER_ID, userId, CLAIM_TYPE, TYPE_REFRESH), refreshTtl);
    }

    public Duration accessTokenTtl() {
        return accessTtl;
    }

    public Duration refreshTokenTtl() {
        return refreshTtl;
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isAccessToken(String token) {
        try {
            return TYPE_ACCESS.equals(parse(token).get(CLAIM_TYPE, String.class));
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return TYPE_REFRESH.equals(parse(token).get(CLAIM_TYPE, String.class));
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Jws<Claims> jws = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return jws.getPayload().getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return parse(token).getSubject();
    }

    public String extractUserId(String token) {
        return parse(token).get(CLAIM_USER_ID, String.class);
    }

    public String extractRole(String token) {
        return parse(token).get(CLAIM_ROLE, String.class);
    }

    private String build(String subject, Map<String, ?> claims, Duration ttl) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .signWith(key)
                .compact();
    }
}
