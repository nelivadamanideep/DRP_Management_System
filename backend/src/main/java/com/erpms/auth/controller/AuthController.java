package com.erpms.auth.controller;

import com.erpms.auth.dto.AuthResponse;
import com.erpms.auth.dto.ForgotPasswordRequest;
import com.erpms.auth.dto.LoginRequest;
import com.erpms.auth.dto.MessageResponse;
import com.erpms.auth.dto.RefreshTokenRequest;
import com.erpms.auth.dto.RegisterRequest;
import com.erpms.auth.dto.ResetPasswordRequest;
import com.erpms.auth.dto.VerifyOtpRequest;
import com.erpms.auth.service.AuthService;
import com.erpms.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Register, login, refresh tokens, forgot / reset password flows")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user account with default GUEST role")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate with email + password; returns access & refresh tokens")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate a refresh token for a fresh access/refresh pair")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Revoke the supplied refresh token (best-effort)")
    public MessageResponse logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return new MessageResponse("Signed out");
    }

    @PostMapping("/logout-all")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Revoke all active refresh tokens for the current user")
    public MessageResponse logoutAll() {
        authService.revokeAllSessions(SecurityUtils.currentUserIdOrNull());
        return new MessageResponse("All sessions revoked");
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Send a 6-digit OTP to the user's email (always returns 200 to avoid enumeration)")
    public Map<String, Object> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String maybeOtp = authService.startForgotPassword(request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "If the account exists, an OTP has been dispatched");
        if (maybeOtp != null) body.put("otp", maybeOtp); // only when erpms.security.reveal-otp-in-response=true
        return body;
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify a password-reset OTP and receive a single-use reset token")
    public Map<String, String> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return Map.of("resetToken", authService.verifyOtp(request));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Consume a reset token and set a new password")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return new MessageResponse("Password has been reset");
    }
}
