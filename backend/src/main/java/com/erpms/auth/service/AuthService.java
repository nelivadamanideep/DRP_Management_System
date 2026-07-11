package com.erpms.auth.service;

import com.erpms.auth.dto.AuthResponse;
import com.erpms.auth.dto.ForgotPasswordRequest;
import com.erpms.auth.dto.LoginRequest;
import com.erpms.auth.dto.RefreshTokenRequest;
import com.erpms.auth.dto.RegisterRequest;
import com.erpms.auth.dto.ResetPasswordRequest;
import com.erpms.auth.dto.VerifyOtpRequest;
import com.erpms.auth.entity.OtpVerificationEntity;
import com.erpms.auth.entity.PasswordResetTokenEntity;
import com.erpms.auth.entity.RefreshTokenEntity;
import com.erpms.auth.repository.OtpVerificationRepository;
import com.erpms.auth.repository.PasswordResetTokenRepository;
import com.erpms.auth.repository.RefreshTokenRepository;
import com.erpms.common.exception.BusinessRuleException;
import com.erpms.common.exception.ResourceNotFoundException;
import com.erpms.common.security.HashUtils;
import com.erpms.notification.service.NotificationService;
import com.erpms.security.JwtService;
import com.erpms.user.entity.UserAccount;
import com.erpms.user.repository.UserAccountRepository;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for the entire authentication lifecycle.
 *
 * <p>Covers registration, login, refresh-token rotation, forgot-password (OTP + reset token)
 * and logout / revoke-all-sessions. All persistence goes through the JPA repositories in
 * {@code com.erpms.auth.repository}; no direct SQL and no raw HTTP calls.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final Duration OTP_TTL = Duration.ofMinutes(10);
    private static final Duration RESET_TOKEN_TTL = Duration.ofMinutes(15);
    private static final int MAX_OTP_ATTEMPTS = 5;

    private final UserAccountRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpVerificationRepository otpRepository;
    private final PasswordResetTokenRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationService notificationService;
    private final String otpPurposePasswordReset = OtpVerificationEntity.Purpose.PASSWORD_RESET.name();
    private final boolean revealOtpInResponse;

    public AuthService(
            UserAccountRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            OtpVerificationRepository otpRepository,
            PasswordResetTokenRepository passwordResetRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            NotificationService notificationService,
            @Value("${erpms.security.reveal-otp-in-response:false}") boolean revealOtpInResponse
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.otpRepository = otpRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.notificationService = notificationService;
        this.revealOtpInResponse = revealOtpInResponse;
    }

    // --------------------------------------------------------------------
    // Register / login
    // --------------------------------------------------------------------

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessRuleException("Email is already registered");
        }

        UserAccount user = new UserAccount();
        user.setEmail(request.email().trim().toLowerCase());
        user.setFullName(request.fullName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole("GUEST");
        user.setStatus("ACTIVE");

        UserAccount saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserAccount user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BadCredentialsException("Account is not active");
        }
        return buildAuthResponse(user);
    }

    // --------------------------------------------------------------------
    // Refresh + logout
    // --------------------------------------------------------------------

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isTokenValid(token) || !jwtService.isRefreshToken(token)) {
            throw new BadCredentialsException("Refresh token is not valid");
        }
        String tokenHash = HashUtils.sha256(token);
        RefreshTokenEntity stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("Refresh token has been revoked"));
        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token is expired or revoked");
        }
        // Rotate: revoke the old, issue a new pair.
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        UserAccount user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> ResourceNotFoundException.of("User", stored.getUserId()));

        return buildAuthResponse(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        refreshTokenRepository.findByTokenHash(HashUtils.sha256(refreshToken))
                .ifPresent(rt -> { rt.setRevoked(true); refreshTokenRepository.save(rt); });
    }

    @Transactional
    public void revokeAllSessions(String userId) {
        refreshTokenRepository.revokeAllForUser(userId);
    }

    // --------------------------------------------------------------------
    // Forgot password / OTP / reset
    // --------------------------------------------------------------------

    /**
     * Kick off a password-reset flow.
     * We deliberately return success even when the email is unknown to prevent
     * account-enumeration attacks. The 6-digit OTP is emailed to the user.
     */
    @Transactional
    public String startForgotPassword(ForgotPasswordRequest request) {
        String otp = HashUtils.numericOtp(6);
        userRepository.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
            OtpVerificationEntity entity = new OtpVerificationEntity();
            entity.setUserId(user.getId());
            entity.setPurpose(otpPurposePasswordReset);
            entity.setCodeHash(HashUtils.sha256(otp));
            entity.setExpiresAt(Instant.now().plus(OTP_TTL));
            otpRepository.save(entity);

            String body = "Your ERPMS password reset code is <b>" + otp + "</b>. "
                    + "It expires in 10 minutes. If you did not request this, please ignore this message.";
            notificationService.sendTransactionalEmail(user.getEmail(),
                    "ERPMS password reset code", "<p>" + body + "</p>");
            log.info("[auth] OTP generated for user='{}' (purpose={})", user.getEmail(), otpPurposePasswordReset);
        });
        return revealOtpInResponse ? otp : null;
    }

    /** Verify the OTP and return a short-lived, single-use password reset token. */
    @Transactional
    public String verifyOtp(VerifyOtpRequest request) {
        UserAccount user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid OTP"));

        OtpVerificationEntity otp = otpRepository
                .findFirstByUserIdAndPurposeAndConsumedFalseOrderByCreatedAtDesc(user.getId(), otpPurposePasswordReset)
                .orElseThrow(() -> new BadCredentialsException("No pending OTP for this account"));

        if (otp.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("OTP has expired");
        }
        if (otp.getAttempts() >= MAX_OTP_ATTEMPTS) {
            otp.setConsumed(true);
            otpRepository.save(otp);
            throw new BadCredentialsException("Too many attempts, please request a new OTP");
        }

        otp.setAttempts(otp.getAttempts() + 1);
        if (!otp.getCodeHash().equals(HashUtils.sha256(request.otp()))) {
            otpRepository.save(otp);
            throw new BadCredentialsException("Invalid OTP");
        }

        otp.setConsumed(true);
        otpRepository.save(otp);

        String resetToken = HashUtils.randomToken(24);
        PasswordResetTokenEntity prt = new PasswordResetTokenEntity();
        prt.setUserId(user.getId());
        prt.setTokenHash(HashUtils.sha256(resetToken));
        prt.setExpiresAt(Instant.now().plus(RESET_TOKEN_TTL));
        passwordResetRepository.save(prt);
        return resetToken;
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        UserAccount user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid reset request"));

        PasswordResetTokenEntity prt = passwordResetRepository
                .findByTokenHash(HashUtils.sha256(request.resetToken()))
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired reset token"));

        if (prt.isConsumed() || prt.getExpiresAt().isBefore(Instant.now())
                || !prt.getUserId().equals(user.getId())) {
            throw new BadCredentialsException("Invalid or expired reset token");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        prt.setConsumed(true);
        passwordResetRepository.save(prt);

        refreshTokenRepository.revokeAllForUser(user.getId());

        notificationService.sendTransactionalEmail(user.getEmail(),
                "ERPMS password changed",
                "<p>Your ERPMS password was changed just now. If this was not you, please contact your administrator.</p>");
    }

    // --------------------------------------------------------------------
    // Helpers
    // --------------------------------------------------------------------

    private AuthResponse buildAuthResponse(UserAccount user) {
        String access = jwtService.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        String refresh = jwtService.generateRefreshToken(user.getEmail(), user.getId());

        RefreshTokenEntity rt = new RefreshTokenEntity();
        rt.setUserId(user.getId());
        rt.setTokenHash(HashUtils.sha256(refresh));
        rt.setExpiresAt(Instant.now().plus(jwtService.refreshTokenTtl()));
        refreshTokenRepository.save(rt);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                access,
                refresh,
                jwtService.accessTokenTtl().toSeconds()
        );
    }
}
