package com.erpms.inventory.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
public class SupplierEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "contact_email", length = 200)
    private String contactEmail;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(length = 400)
    private String address;

    @Column(name = "gst_number", length = 60)
    private String gstNumber;

    @Column(nullable = false)
    private boolean active = true;

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String v) { this.contactEmail = v; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String v) { this.contactPhone = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String v) { this.gstNumber = v; }
    public boolean isActive() { return active; }
    public void setActive(boolean v) { this.active = v; }
}
