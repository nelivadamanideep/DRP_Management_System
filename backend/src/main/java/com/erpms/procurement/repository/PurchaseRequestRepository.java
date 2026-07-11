package com.erpms.procurement.repository;

import com.erpms.procurement.entity.PurchaseRequestEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequestEntity, String> {
    boolean existsByRequestNumberIgnoreCase(String number);
    List<PurchaseRequestEntity> findByStatus(String status);
    List<PurchaseRequestEntity> findByRequestedByUserIdOrderByCreatedAtDesc(String userId);
}
