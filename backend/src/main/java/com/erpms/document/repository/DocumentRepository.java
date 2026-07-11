package com.erpms.document.repository;

import com.erpms.document.entity.DocumentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, String> {
    List<DocumentEntity> findByProjectId(String projectId);
    List<DocumentEntity> findByFolderId(String folderId);
    List<DocumentEntity> findByOwnerUserId(String ownerUserId);
    List<DocumentEntity> findByTitleContainingIgnoreCase(String query);
}
