package com.example.recipes.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class FileStorageService {
    private final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final String imageStorageLocation;

    public FileStorageService(@Value("${app.storage.location") String storageLocation) {
        this.imageStorageLocation = storageLocation + "/img/";
        Path imageStoragePath = Path.of(this.imageStorageLocation);
        prepareStorageDirectories(imageStorageLocation);
    }

    private void prepareStorageDirectories(String imageStorageLocation) {

    }
}
