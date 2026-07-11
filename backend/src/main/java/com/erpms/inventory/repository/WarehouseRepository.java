package com.erpms.inventory.repository;

import com.erpms.inventory.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<WarehouseEntity, String> {
    boolean existsByCodeIgnoreCase(String code);
}
