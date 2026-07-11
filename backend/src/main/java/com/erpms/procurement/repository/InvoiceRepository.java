package com.erpms.procurement.repository;

import com.erpms.procurement.entity.InvoiceEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, String> {
    boolean existsByInvoiceNumberIgnoreCase(String number);
    List<InvoiceEntity> findByPurchaseOrderId(String purchaseOrderId);
    List<InvoiceEntity> findByStatus(String status);
}
