package com.erpms.procurement.repository;

import com.erpms.procurement.entity.PurchaseOrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, String> {
    boolean existsByPoNumberIgnoreCase(String number);
    List<PurchaseOrderEntity> findBySupplierId(String supplierId);
    List<PurchaseOrderEntity> findByStatus(String status);
}
