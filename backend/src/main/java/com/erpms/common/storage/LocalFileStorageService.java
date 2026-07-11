package com.erpms.common.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Local-disk implementation of {@link FileStorageService}.
 *
 * <p>Chosen deliberately as the default so the platform is fully functional
 * on a bare Docker Compose install without any object-storage credentials.
 * Swap out for a MinIO/S3 bean when you're ready to scale horizontally.
 */
@Component
public class LocalFileStorageService implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageService.class);

    private final Path root;

    public LocalFileStorageService(@Value("${erpms.storage.local-root:/var/lib/erpms/storage}") String rootPath) throws IOException {
        this.root = Paths.get(rootPath);
        Files.createDirectories(root);
        log.info("[storage] using local root '{}'", root);
    }

    @Override
    public StoredObject store(String prefix, String fileName, String contentType, InputStream data) throws IOException {
        String safePrefix = prefix == null ? "misc" : prefix.replaceAll("[^A-Za-z0-9_/\\-]", "_");
        String uniqueName = UUID.randomUUID() + "-" + sanitize(fileName);
        Path target = root.resolve(safePrefix).resolve(uniqueName).normalize();
        if (!target.startsWith(root)) throw new IOException("Path traversal detected");
        Files.createDirectories(target.getParent());

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }

        long total = 0;
        try (InputStream in = data;
             OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW)) {
            byte[] buf = new byte[8192];
            int read;
            while ((read = in.read(buf)) > 0) {
                digest.update(buf, 0, read);
                out.write(buf, 0, read);
                total += read;
            }
        }
        String sha = HexFormat.of().formatHex(digest.digest());
        String key = root.relativize(target).toString().replace('\\', '/');
        log.info("[storage] stored '{}' ({} bytes, sha256={})", key, total, sha);
        return new StoredObject(key, total, sha, contentType, sanitize(fileName));
    }

    @Override
    public InputStream openStream(String storageKey) throws IOException {
        Path p = root.resolve(storageKey).normalize();
        if (!p.startsWith(root)) throw new IOException("Path traversal detected");
        return Files.newInputStream(p);
    }

    @Override
    public void delete(String storageKey) {
        try {
            Path p = root.resolve(storageKey).normalize();
            if (p.startsWith(root)) Files.deleteIfExists(p);
        } catch (IOException ex) {
            log.warn("[storage] delete failed for '{}': {}", storageKey, ex.getMessage());
        }
    }

    private String sanitize(String name) {
        if (name == null || name.isBlank()) return "file";
        return name.replaceAll("[^A-Za-z0-9._\\-]", "_");
    }
}
