package com.jlmasa.pms_backend.files.storage;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Component
public class LocalDiskFileStorage implements FileStorage{

    private final Path root;

    public LocalDiskFileStorage(){
        String dir = System.getenv("PMS_STORAGE_DIR");
        if (dir == null || dir.isBlank()){
            throw new IllegalStateException("PMS_STORAGE_DIR must be set.");
        }
        this.root = Path.of(dir);
        try{
            Files.createDirectories(root);
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String store(InputStream data, long sizeBytes) throws IOException {
        String key = UUID.randomUUID().toString();
        Path target = resolve(key);
        Files.createDirectories(target.getParent());
        try (OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW)){
            data.transferTo(out);
        }
        return key;
    }
    @Override
    public InputStream open(String storageKey) throws IOException {
        return Files.newInputStream(resolve(storageKey));
    }
    @Override
    public void delete(String storageKey) throws IOException {
        Files.deleteIfExists(resolve(storageKey));
    }

    private Path resolve(String storageKey){
        UUID.fromString(storageKey);
        Path candidate = root.resolve(storageKey.substring(0,2)).resolve(storageKey);
        if(!candidate.normalize().startsWith(root.normalize())){
            throw new SecurityException("Resolved path escapes the storage root.");
        }
        return candidate;
    }
}
