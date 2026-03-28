package com.rubilia.exercise201.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${uploads.directory}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Log để kiểm tra
            System.out.println("Upload directory: " + uploadDir);
            System.out.println("File name: " + file.getOriginalFilename());

            // Tạo thư mục uploads nếu chưa tồn tại
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                System.out.println("Created upload directory: " + created);
            }

            // Tạo tên file duy nhất
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            File destinationFile = new File(uploadDir + File.separator + fileName);

            // Lưu file vào thư mục uploads
            file.transferTo(destinationFile);

            // Tạo URL của file
            String fileUrl = "/uploads/" + fileName;

            return ResponseEntity.ok(new UploadResponse(fileUrl));
        } catch (IOException e) {
            System.err.println("IOException during file upload: " + e.getMessage());
            return ResponseEntity.status(500).body("Lỗi khi tải file lên: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during file upload: " + e.getMessage());
            return ResponseEntity.status(500).body("Lỗi không xác định khi tải file lên: " + e.getMessage());
        }
    }
}

class UploadResponse {
    private String url;

    public UploadResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}