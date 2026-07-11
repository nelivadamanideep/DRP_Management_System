package com.erpms.document.repository;

import com.erpms.document.entity.DocumentVersionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersionEntity, String> {
    List<DocumentVersionEntity> findByDocumentIdOrderByVersionNumberDesc(String documentId);
    Optional<DocumentVersionEntity> findFirstByDocumentIdOrderByVersionNumberDesc(String documentId);
}
