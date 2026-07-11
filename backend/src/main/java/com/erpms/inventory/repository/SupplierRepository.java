package com.erpms.inventory.repository;

import com.erpms.inventory.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<SupplierEntity, String> {
}
