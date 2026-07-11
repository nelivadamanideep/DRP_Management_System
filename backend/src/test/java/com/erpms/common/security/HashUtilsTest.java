package com.erpms.common.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashUtilsTest {

    @Test
    void sha256IsDeterministic() {
        String a = HashUtils.sha256("hello");
        String b = HashUtils.sha256("hello");
        assertEquals(a, b);
        assertEquals(64, a.length());
    }

    @Test
    void otpMatchesRequestedLength() {
        assertEquals(6, HashUtils.numericOtp(6).length());
        assertTrue(HashUtils.numericOtp(6).matches("\\d{6}"));
    }

    @Test
    void randomTokenIsHex() {
        String t = HashUtils.randomToken(24);
        assertEquals(48, t.length());
        assertTrue(t.matches("[0-9a-f]+"));
    }
}
