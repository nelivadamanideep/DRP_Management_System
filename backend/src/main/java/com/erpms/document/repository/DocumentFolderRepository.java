package com.erpms.document.repository;

import com.erpms.document.entity.DocumentFolderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentFolderRepository extends JpaRepository<DocumentFolderEntity, String> {
    List<DocumentFolderEntity> findByParentId(String parentId);
    List<DocumentFolderEntity> findByParentIdIsNull();
    List<DocumentFolderEntity> findByProjectId(String projectId);
}
