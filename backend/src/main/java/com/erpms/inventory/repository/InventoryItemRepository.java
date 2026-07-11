package com.erpms.inventory.repository;

import com.erpms.inventory.entity.InventoryItemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryItemRepository extends JpaRepository<InventoryItemEntity, String> {
    boolean existsBySkuIgnoreCase(String sku);
    List<InventoryItemEntity> findByWarehouseId(String warehouseId);

    @Query("SELECT i FROM InventoryItemEntity i WHERE i.stockQuantity <= i.reorderLevel")
    List<InventoryItemEntity> findLowStock();
}
