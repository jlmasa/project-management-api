package com.jlmasa.pms_backend.files.storage;

import java.io.InputStream;

public interface FileStorage {

    String store(InputStream data, long sizeBytes) throws java.io.IOException;

    InputStream open(String storageKey) throws java.io.IOException;

    void delete(String storageKey) throws java.io.IOException;
}
