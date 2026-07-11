package com.erpms.document.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.util.UUID;

/** Hierarchical folder used to organise documents. */
@Entity
@Table(name = "document_folders", indexes = {
        @Index(name = "idx_document_folders_parent", columnList = "parent_id")
})
public class DocumentFolderEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, length = 180)
    private String name;

    @Column(name = "parent_id", length = 36)
    private String parentId;

    @Column(name = "project_id", length = 36)
    private String projectId;

    @Column(name = "path", nullable = false, length = 1000)
    private String path;

    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}
