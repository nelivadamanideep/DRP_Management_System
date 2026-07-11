package com.erpms.document.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Root document record. The actual bytes for each version live on
 * {@link DocumentVersionEntity}, so a document can carry many revisions
 * without duplicating metadata.
 */
@Entity
@Table(name = "documents", indexes = {
        @Index(name = "idx_documents_project", columnList = "project_id"),
        @Index(name = "idx_documents_folder", columnList = "folder_id"),
        @Index(name = "idx_documents_status", columnList = "status")
})
public class DocumentEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, length = 240)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "document_type", nullable = false, length = 40)
    private String documentType;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "project_id", length = 36)
    private String projectId;

    @Column(name = "folder_id", length = 36)
    private String folderId;

    @Column(name = "owner_user_id", nullable = false, length = 36)
    private String ownerUserId;

    @Column(name = "current_version_id", length = 36)
    private String currentVersionId;

    @Column(nullable = false)
    private boolean confidential = false;

    public String getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getFolderId() { return folderId; }
    public void setFolderId(String folderId) { this.folderId = folderId; }

    public String getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(String ownerUserId) { this.ownerUserId = ownerUserId; }

    public String getCurrentVersionId() { return currentVersionId; }
    public void setCurrentVersionId(String currentVersionId) { this.currentVersionId = currentVersionId; }

    public boolean isConfidential() { return confidential; }
    public void setConfidential(boolean confidential) { this.confidential = confidential; }
}
