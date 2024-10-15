package com.example.doc_agent.file.service.util;

import com.example.doc_agent.file.exception.FileUploadException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ResourceReader {
    private ResourceReader() {
    }

    public static ByteArrayResource read(MultipartFile file) {
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new FileUploadException("Failed to read file", e);
        }

    }
}
