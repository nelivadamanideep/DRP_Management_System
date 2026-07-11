package com.erpms.inventory.repository;

import com.erpms.inventory.entity.StockMovementEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovementEntity, String> {
    List<StockMovementEntity> findByItemIdOrderByCreatedAtDesc(String itemId);
}
