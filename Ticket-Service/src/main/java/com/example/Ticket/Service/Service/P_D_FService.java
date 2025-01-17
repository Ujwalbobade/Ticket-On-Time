package com.example.Ticket.Service.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class P_D_FService {


    private static final String LOCAL_STORAGE_PATH = "C:/Users/u.ravindra.bobade/Downloads/Tickets Files"; // Set this to your desired local storage path
    private final Map<String, String> filePaths = new HashMap<>(); // Simple in-memory store for file paths

    public String uploadFile(MultipartFile file) throws IOException {
        // Ensure the directory exists
        File directory = new File(LOCAL_STORAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Define the path where the file will be stored
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(LOCAL_STORAGE_PATH, fileName);

        // Save the file to the local filesystem
        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Store the path for future reference
        filePaths.put(fileName, filePath.toString());

        return filePath.toString();
    }

    public Resource downloadFile(String fileName) throws IOException {
        // Retrieve the stored path
        String filePath = filePaths.get(fileName);
        if (filePath == null) {
            throw new IOException("File not found: " + fileName);
        }

        // Create a Path object and get the InputStream
        Path path = Paths.get(filePath);
        InputStream inputStream = Files.newInputStream(path);

        // Return as an InputStreamResource
        return new InputStreamResource(inputStream);
    }
}
