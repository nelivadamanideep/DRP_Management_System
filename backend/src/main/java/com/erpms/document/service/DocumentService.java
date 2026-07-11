package com.erpms.document.service;

import com.erpms.common.exception.BusinessRuleException;
import com.erpms.common.exception.ResourceNotFoundException;
import com.erpms.common.security.SecurityUtils;
import com.erpms.common.storage.FileStorageService;
import com.erpms.document.dto.*;
import com.erpms.document.entity.DocumentEntity;
import com.erpms.document.entity.DocumentFolderEntity;
import com.erpms.document.entity.DocumentVersionEntity;
import com.erpms.document.enums.DocumentStatus;
import com.erpms.document.repository.DocumentFolderRepository;
import com.erpms.document.repository.DocumentRepository;
import com.erpms.document.repository.DocumentVersionRepository;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Document Management application service.
 *
 * <p>Handles folder hierarchy, metadata CRUD, version upload/download and
 * approval-workflow status transitions.
 */
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final DocumentFolderRepository folderRepository;
    private final FileStorageService storage;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentVersionRepository versionRepository,
            DocumentFolderRepository folderRepository,
            FileStorageService storage
    ) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
        this.folderRepository = folderRepository;
        this.storage = storage;
    }

    // ---- Folder tree ----------------------------------------------------

    @Transactional
    public FolderResponse createFolder(FolderCreateRequest request) {
        DocumentFolderEntity folder = new DocumentFolderEntity();
        folder.setName(request.name().trim());
        folder.setParentId(request.parentId());
        folder.setProjectId(request.projectId());

        String path;
        if (request.parentId() == null) {
            path = "/" + folder.getName();
        } else {
            DocumentFolderEntity parent = folderRepository.findById(request.parentId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Folder", request.parentId()));
            path = parent.getPath() + "/" + folder.getName();
        }
        folder.setPath(path);
        return toFolderResponse(folderRepository.save(folder));
    }

    @Transactional(readOnly = true)
    public List<FolderResponse> listRootFolders() {
        return folderRepository.findByParentIdIsNull().stream().map(this::toFolderResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FolderResponse> listChildFolders(String parentId) {
        return folderRepository.findByParentId(parentId).stream().map(this::toFolderResponse).toList();
    }

    // ---- Documents ------------------------------------------------------

    @Transactional
    public DocumentResponse createDocument(DocumentCreateRequest request) {
        String userId = requireUserId();
        DocumentEntity doc = new DocumentEntity();
        doc.setTitle(request.title().trim());
        doc.setDescription(request.description());
        doc.setDocumentType(request.documentType().trim().toUpperCase());
        doc.setProjectId(request.projectId());
        doc.setFolderId(request.folderId());
        doc.setOwnerUserId(userId);
        doc.setConfidential(Boolean.TRUE.equals(request.confidential()));
        doc.setStatus(DocumentStatus.DRAFT.name());
        return toResponse(documentRepository.save(doc));
    }

    @Transactional(readOnly = true)
    public DocumentResponse findById(String id) {
        return toResponse(documentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Document", id)));
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> findAll() {
        return documentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> search(String query) {
        if (query == null || query.isBlank()) return findAll();
        return documentRepository.findByTitleContainingIgnoreCase(query.trim()).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public DocumentResponse updateStatus(String id, String newStatus) {
        DocumentEntity doc = documentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Document", id));
        DocumentStatus target;
        try {
            target = DocumentStatus.valueOf(newStatus.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("Unknown document status: " + newStatus);
        }
        doc.setStatus(target.name());
        return toResponse(documentRepository.save(doc));
    }

    @Transactional
    public void delete(String id) {
        DocumentEntity doc = documentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Document", id));
        versionRepository.findByDocumentIdOrderByVersionNumberDesc(id)
                .forEach(v -> storage.delete(v.getStorageKey()));
        versionRepository.deleteAll(versionRepository.findByDocumentIdOrderByVersionNumberDesc(id));
        documentRepository.delete(doc);
    }

    // ---- Versions -------------------------------------------------------

    @Transactional
    public DocumentVersionResponse uploadVersion(String documentId, MultipartFile file, String changelog) throws IOException {
        DocumentEntity doc = documentRepository.findById(documentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Document", documentId));
        if (file == null || file.isEmpty()) throw new BusinessRuleException("Uploaded file is empty");

        int nextVersion = versionRepository.findFirstByDocumentIdOrderByVersionNumberDesc(documentId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        FileStorageService.StoredObject stored;
        try (var in = file.getInputStream()) {
            stored = storage.store("documents/" + documentId, file.getOriginalFilename(),
                    file.getContentType(), in);
        }

        DocumentVersionEntity v = new DocumentVersionEntity();
        v.setDocumentId(documentId);
        v.setVersionNumber(nextVersion);
        v.setStorageKey(stored.storageKey());
        v.setFileName(stored.fileName());
        v.setContentType(stored.contentType());
        v.setSizeBytes(stored.sizeBytes());
        v.setContentSha256(stored.sha256());
        v.setUploadedByUserId(requireUserId());
        v.setChangelog(changelog);
        DocumentVersionEntity saved = versionRepository.save(v);

        doc.setCurrentVersionId(saved.getId());
        documentRepository.save(doc);
        return toVersionResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DocumentVersionResponse> listVersions(String documentId) {
        return versionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId)
                .stream().map(this::toVersionResponse).toList();
    }

    @Transactional(readOnly = true)
    public DocumentVersionEntity requireVersion(String versionId) {
        return versionRepository.findById(versionId)
                .orElseThrow(() -> ResourceNotFoundException.of("DocumentVersion", versionId));
    }

    // ---- Mappers --------------------------------------------------------

    private DocumentResponse toResponse(DocumentEntity d) {
        return new DocumentResponse(
                d.getId(), d.getTitle(), d.getDescription(), d.getDocumentType(), d.getStatus(),
                d.getProjectId(), d.getFolderId(), d.getOwnerUserId(), d.getCurrentVersionId(),
                d.isConfidential(), d.getCreatedAt(), d.getUpdatedAt()
        );
    }

    private DocumentVersionResponse toVersionResponse(DocumentVersionEntity v) {
        return new DocumentVersionResponse(
                v.getId(), v.getDocumentId(), v.getVersionNumber(), v.getFileName(), v.getContentType(),
                v.getSizeBytes(), v.getContentSha256(), v.getUploadedByUserId(), v.getChangelog(), v.getCreatedAt()
        );
    }

    private FolderResponse toFolderResponse(DocumentFolderEntity f) {
        return new FolderResponse(f.getId(), f.getName(), f.getParentId(), f.getProjectId(),
                f.getPath(), f.getCreatedAt());
    }

    private String requireUserId() {
        String id = SecurityUtils.currentUserIdOrNull();
        if (id == null) throw new BusinessRuleException("No authenticated user in current context");
        return id;
    }
}
