package com.erpms.equipment.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "equipment_maintenance_logs", indexes = {
        @Index(name = "idx_maintenance_equipment", columnList = "equipment_id")
})
public class EquipmentMaintenanceEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "equipment_id", nullable = false, length = 36)
    private String equipmentId;

    @Column(name = "performed_by_user_id", length = 36)
    private String performedByUserId;

    @Column(name = "performed_on", nullable = false)
    private LocalDate performedOn;

    @Column(nullable = false, length = 40)
    private String activity; // MAINTENANCE, CALIBRATION, REPAIR, INSPECTION

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "next_due_on")
    private LocalDate nextDueOn;

    public String getId() { return id; }
    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String v) { this.equipmentId = v; }
    public String getPerformedByUserId() { return performedByUserId; }
    public void setPerformedByUserId(String v) { this.performedByUserId = v; }
    public LocalDate getPerformedOn() { return performedOn; }
    public void setPerformedOn(LocalDate v) { this.performedOn = v; }
    public String getActivity() { return activity; }
    public void setActivity(String v) { this.activity = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public LocalDate getNextDueOn() { return nextDueOn; }
    public void setNextDueOn(LocalDate v) { this.nextDueOn = v; }
}
