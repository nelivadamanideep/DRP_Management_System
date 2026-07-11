package com.erpms.document.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * A single stored revision of a {@link DocumentEntity}. The blob itself lives
 * on the configured storage backend (local disk or S3/MinIO) — this row keeps
 * the reference and metadata only.
 */
@Entity
@Table(name = "document_versions", indexes = {
        @Index(name = "idx_document_versions_doc", columnList = "document_id"),
        @Index(name = "idx_document_versions_hash", columnList = "content_sha256")
})
public class DocumentVersionEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "document_id", nullable = false, length = 36)
    private String documentId;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", length = 200)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "content_sha256", length = 64)
    private String contentSha256;

    @Column(name = "uploaded_by_user_id", nullable = false, length = 36)
    private String uploadedByUserId;

    @Column(columnDefinition = "text")
    private String changelog;

    public String getId() { return id; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public int getVersionNumber() { return versionNumber; }
    public void setVersionNumber(int versionNumber) { this.versionNumber = versionNumber; }

    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    public String getContentSha256() { return contentSha256; }
    public void setContentSha256(String contentSha256) { this.contentSha256 = contentSha256; }

    public String getUploadedByUserId() { return uploadedByUserId; }
    public void setUploadedByUserId(String uploadedByUserId) { this.uploadedByUserId = uploadedByUserId; }

    public String getChangelog() { return changelog; }
    public void setChangelog(String changelog) { this.changelog = changelog; }
}
