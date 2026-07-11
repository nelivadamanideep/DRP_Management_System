package com.erpms.common.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * File storage abstraction. The default implementation writes to a local
 * directory; a MinIO / S3 flavour can be plugged in transparently by
 * providing another Spring bean of this type.
 */
public interface FileStorageService {

    /**
     * Persist the supplied stream at a stable location within the storage
     * root and return the storage-relative key.
     */
    StoredObject store(String logicalKeyPrefix, String fileName, String contentType, InputStream data) throws IOException;

    /** Open the stored object for reading. Caller must close the stream. */
    InputStream openStream(String storageKey) throws IOException;

    /** Best-effort deletion; must not throw when the key is already absent. */
    void delete(String storageKey);

    /** Metadata returned to callers after a successful store operation. */
    record StoredObject(String storageKey, long sizeBytes, String sha256, String contentType, String fileName) {}
}
