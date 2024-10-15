package com.example.doc_agent.file.service;

import com.example.doc_agent.file.exception.FileUploadException;
import com.example.doc_agent.file.persistence.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file provided");
        }

        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (!"txt".equalsIgnoreCase(fileExtension) && !"pdf".equalsIgnoreCase(fileExtension)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only .txt or .pdf files are allowed");
        }
    }

    public UUID save(MultipartFile file) {
        validate(file);

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            file.transferTo(new File(directory + file.getOriginalFilename()));
            logger.info("File uploaded successfully: {}", file.getOriginalFilename());
            return fileRepository.add(file.getOriginalFilename());
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload file", e);
        }
    }

    public String getFilePath(UUID id) {
        String resource = fileRepository.getFileName(id);
        if (resource == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        return uploadDir + "/" + resource;
    }
}
