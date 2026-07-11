package com.erpms.equipment.repository;

import com.erpms.equipment.entity.EquipmentBookingEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipmentBookingRepository extends JpaRepository<EquipmentBookingEntity, String> {

    List<EquipmentBookingEntity> findByEquipmentIdOrderByStartTimeDesc(String equipmentId);
    List<EquipmentBookingEntity> findByBookedByUserIdOrderByStartTimeDesc(String userId);

    /** Returns overlapping bookings for the same equipment (excluding CANCELLED). */
    @Query("""
           SELECT b FROM EquipmentBookingEntity b
           WHERE b.equipmentId = :equipmentId
             AND b.status <> 'CANCELLED'
             AND b.startTime < :end
             AND b.endTime > :start
           """)
    List<EquipmentBookingEntity> findOverlapping(
            @Param("equipmentId") String equipmentId,
            @Param("start") Instant start,
            @Param("end") Instant end);
}
