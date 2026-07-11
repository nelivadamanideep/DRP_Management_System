package com.erpms.user.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "user_id", nullable = false, unique = true, length = 36)
    private String userId;

    @Column(length = 120)
    private String designation;

    @Column(name = "department_id", length = 36)
    private String departmentId;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears = 0;

    @Column(columnDefinition = "text")
    private String skills;

    @Column(columnDefinition = "text")
    private String certifications;

    @Column(length = 30)
    private String phone;

    public String getId() { return id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}