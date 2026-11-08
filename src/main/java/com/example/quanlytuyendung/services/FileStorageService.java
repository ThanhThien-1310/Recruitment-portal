package com.example.quanlytuyendung.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads", "cv");
    private final Path offerDir = Paths.get("uploads", "offers");

    public FileStorageService() {
        try {
            Files.createDirectories(uploadDir);
            Files.createDirectories(offerDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String storeCv(MultipartFile file, String candidateName) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String safeName = candidateName.replaceAll("[^a-zA-Z0-9]", "_");
        String filename = "CV_" + safeName + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
        Path targetPath = this.uploadDir.resolve(filename);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/cv/" + filename;
    }

    public Path getCvPath(String filename) {
        return this.uploadDir.resolve(filename);
    }

    public Path getOfferPath(String filename) {
        return this.offerDir.resolve(filename);
    }
}
