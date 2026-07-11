package com.erpms.equipment.repository;

import com.erpms.equipment.entity.EquipmentMaintenanceEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentMaintenanceRepository extends JpaRepository<EquipmentMaintenanceEntity, String> {
    List<EquipmentMaintenanceEntity> findByEquipmentIdOrderByPerformedOnDesc(String equipmentId);
}
