package com.example.recipes.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileStorageService {
    private final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final String imageStorageLocation;

    public FileStorageService(@Value("${app.storage.location") String storageLocation) {
        this.imageStorageLocation = storageLocation + "/img/";
        Path imageStoragePath = Path.of(this.imageStorageLocation);
        prepareStorageDirectories(imageStoragePath);
    }

    private void prepareStorageDirectories(Path imageStoragePath) {
        try {
            if (Files.notExists(imageStoragePath)){
                Files.createDirectories(imageStoragePath);
                logger.info("Storage directory for images created %S".formatted(imageStoragePath.toAbsolutePath().toString()));
            }
        } catch (IOException e){
            throw new UncheckedIOException("Creation of image storage failed", e);
        }




    }
}
