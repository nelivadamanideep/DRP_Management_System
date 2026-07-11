package com.erpms.team.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "project_team_members")
public class ProjectTeamMember {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "project_id", nullable = false, length = 36)
    private String projectId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "role_in_project", nullable = false, length = 80)
    private String roleInProject;

    @Column(name = "allocation_percent", nullable = false)
    private Integer allocationPercent = 100;

    @Column(nullable = false)
    private Boolean active = true;

    public String getId() { return id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRoleInProject() { return roleInProject; }
    public void setRoleInProject(String roleInProject) { this.roleInProject = roleInProject; }

    public Integer getAllocationPercent() { return allocationPercent; }
    public void setAllocationPercent(Integer allocationPercent) { this.allocationPercent = allocationPercent; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}