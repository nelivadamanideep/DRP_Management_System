package com.erpms.auth.repository;

import com.erpms.auth.entity.OtpVerificationEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpVerificationRepository extends JpaRepository<OtpVerificationEntity, String> {

    Optional<OtpVerificationEntity> findFirstByUserIdAndPurposeAndConsumedFalseOrderByCreatedAtDesc(
            String userId, String purpose);
}
