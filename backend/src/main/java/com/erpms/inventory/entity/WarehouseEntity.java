package com.erpms.inventory.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "warehouses")
public class WarehouseEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 300)
    private String location;

    @Column(name = "manager_user_id", length = 36)
    private String managerUserId;

    @Column(nullable = false)
    private boolean active = true;

    public String getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String v) { this.code = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getLocation() { return location; }
    public void setLocation(String v) { this.location = v; }
    public String getManagerUserId() { return managerUserId; }
    public void setManagerUserId(String v) { this.managerUserId = v; }
    public boolean isActive() { return active; }
    public void setActive(boolean v) { this.active = v; }
}
