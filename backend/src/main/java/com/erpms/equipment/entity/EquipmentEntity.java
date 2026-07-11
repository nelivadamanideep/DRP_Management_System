package com.erpms.equipment.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "equipment", indexes = {
        @Index(name = "idx_equipment_status", columnList = "status"),
        @Index(name = "idx_equipment_department", columnList = "department_id")
})
public class EquipmentEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "asset_tag", nullable = false, unique = true, length = 60)
    private String assetTag;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 120)
    private String manufacturer;

    @Column(name = "model_number", length = 120)
    private String modelNumber;

    @Column(name = "serial_number", length = 120)
    private String serialNumber;

    @Column(name = "department_id", length = 36)
    private String departmentId;

    @Column(name = "laboratory_location", length = 200)
    private String laboratoryLocation;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "next_calibration_date")
    private LocalDate nextCalibrationDate;

    @Column(name = "qr_code_payload", length = 400)
    private String qrCodePayload;

    @Column(nullable = false, length = 30)
    private String status = "AVAILABLE";

    public String getId() { return id; }
    public String getAssetTag() { return assetTag; }
    public void setAssetTag(String v) { this.assetTag = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String v) { this.manufacturer = v; }
    public String getModelNumber() { return modelNumber; }
    public void setModelNumber(String v) { this.modelNumber = v; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String v) { this.serialNumber = v; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String v) { this.departmentId = v; }
    public String getLaboratoryLocation() { return laboratoryLocation; }
    public void setLaboratoryLocation(String v) { this.laboratoryLocation = v; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate v) { this.purchaseDate = v; }
    public LocalDate getNextCalibrationDate() { return nextCalibrationDate; }
    public void setNextCalibrationDate(LocalDate v) { this.nextCalibrationDate = v; }
    public String getQrCodePayload() { return qrCodePayload; }
    public void setQrCodePayload(String v) { this.qrCodePayload = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
}
