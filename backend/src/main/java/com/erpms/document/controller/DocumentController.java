package com.erpms.document.controller;

import com.erpms.document.dto.*;
import com.erpms.document.entity.DocumentVersionEntity;
import com.erpms.document.service.DocumentService;
import com.erpms.common.storage.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documents")
@Tag(name = "Documents", description = "Document library, folder tree and version management")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    private final DocumentService service;
    private final FileStorageService storage;

    public DocumentController(DocumentService service, FileStorageService storage) {
        this.service = service;
        this.storage = storage;
    }

    // ---- Folders ---------------------------------------------------

    @PostMapping("/folders")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new folder in the document tree")
    public FolderResponse createFolder(@Valid @RequestBody FolderCreateRequest request) {
        return service.createFolder(request);
    }

    @GetMapping("/folders")
    @Operation(summary = "List folders at the root of the document tree")
    public List<FolderResponse> listRoots() {
        return service.listRootFolders();
    }

    @GetMapping("/folders/{parentId}/children")
    @Operation(summary = "List direct children of a folder")
    public List<FolderResponse> listChildren(@PathVariable String parentId) {
        return service.listChildFolders(parentId);
    }

    // ---- Documents -------------------------------------------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new document (metadata only; upload the first version separately)")
    public DocumentResponse create(@Valid @RequestBody DocumentCreateRequest request) {
        return service.createDocument(request);
    }

    @GetMapping
    @Operation(summary = "List documents (optionally filtered by title substring)")
    public List<DocumentResponse> list(@RequestParam(required = false) String q) {
        return service.search(q);
    }

    @GetMapping("/{id}")
    public DocumentResponse findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Advance a document through the approval workflow")
    public DocumentResponse updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        return service.updateStatus(id, body.getOrDefault("status", ""));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    // ---- Versions --------------------------------------------------

    @PostMapping(value = "/{id}/versions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload a new binary version of the document")
    public DocumentVersionResponse uploadVersion(
            @PathVariable String id,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "changelog", required = false) String changelog
    ) throws IOException {
        return service.uploadVersion(id, file, changelog);
    }

    @GetMapping("/{id}/versions")
    public List<DocumentVersionResponse> listVersions(@PathVariable String id) {
        return service.listVersions(id);
    }

    @GetMapping("/versions/{versionId}/download")
    @Operation(summary = "Download the binary payload for a specific document version")
    public ResponseEntity<InputStreamResource> download(@PathVariable String versionId) throws IOException {
        DocumentVersionEntity version = service.requireVersion(versionId);
        InputStream stream = storage.openStream(version.getStorageKey());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(org.springframework.http.ContentDisposition
                .attachment().filename(version.getFileName()).build());
        MediaType type = version.getContentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(version.getContentType());
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(type)
                .contentLength(version.getSizeBytes())
                .body(new InputStreamResource(stream));
    }
}
