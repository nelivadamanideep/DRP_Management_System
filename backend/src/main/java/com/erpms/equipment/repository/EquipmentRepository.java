package com.erpms.equipment.repository;

import com.erpms.equipment.entity.EquipmentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, String> {
    boolean existsByAssetTagIgnoreCase(String assetTag);
    List<EquipmentEntity> findByDepartmentId(String departmentId);
    List<EquipmentEntity> findByStatus(String status);
    long countByStatus(String status);
}
