package com.erpms.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Small helpers used by the auth module for one-way hashing and secure
 * random-code generation. Isolated here so that callers never depend on
 * {@code java.security.*} directly.
 */
public final class HashUtils {

    private static final SecureRandom RANDOM = new SecureRandom();

    private HashUtils() {}

    /** Return the hex-encoded SHA-256 of the supplied UTF-8 string. */
    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }

    /** Generate a numeric one-time password of the given length. */
    public static String numericOtp(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /** Generate a URL-safe random token of the given byte length (hex-encoded). */
    public static String randomToken(int byteLength) {
        byte[] bytes = new byte[byteLength];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
