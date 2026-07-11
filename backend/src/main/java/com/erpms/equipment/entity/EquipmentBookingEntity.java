package com.erpms.equipment.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "equipment_bookings", indexes = {
        @Index(name = "idx_bookings_equipment", columnList = "equipment_id"),
        @Index(name = "idx_bookings_user", columnList = "booked_by_user_id"),
        @Index(name = "idx_bookings_range", columnList = "start_time,end_time")
})
public class EquipmentBookingEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "equipment_id", nullable = false, length = 36)
    private String equipmentId;

    @Column(name = "booked_by_user_id", nullable = false, length = 36)
    private String bookedByUserId;

    @Column(name = "project_id", length = 36)
    private String projectId;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(nullable = false, length = 30)
    private String status = "SCHEDULED";

    @Column(columnDefinition = "text")
    private String purpose;

    public String getId() { return id; }
    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String v) { this.equipmentId = v; }
    public String getBookedByUserId() { return bookedByUserId; }
    public void setBookedByUserId(String v) { this.bookedByUserId = v; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String v) { this.projectId = v; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant v) { this.startTime = v; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant v) { this.endTime = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String v) { this.purpose = v; }
}
