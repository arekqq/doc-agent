package com.example.doc_agent.file.service;

import com.example.doc_agent.file.dto.UploadFileStatus;
import com.example.doc_agent.file.dto.UploadResult;
import com.example.doc_agent.file.persistence.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        if (!"txt".equalsIgnoreCase(fileExtension) && !"pdf".equalsIgnoreCase(fileExtension)) { // TODO create collection of supported file extensions
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only .txt or .pdf files are allowed");
        }
    }

    public UploadResult save(MultipartFile file) {


        validate(file);

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String id = null;
        UploadFileStatus status;
        try {
            file.transferTo(new File(directory + file.getOriginalFilename()));
            logger.info("File uploaded successfully: {}", file.getOriginalFilename());
            id = fileRepository.add(file.getOriginalFilename()).toString();
            status = UploadFileStatus.SUCCESS;
        } catch (IOException e) {
            status = UploadFileStatus.FAILURE;
            logger.info("File upload failed: {}", file.getOriginalFilename());
        }
        return new UploadResult(status, id);
    }

    public String getFilePath(UUID id) {
        String resource = fileRepository.getFileName(id);
        if (resource == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        return uploadDir + "/" + resource;
    }

    public Resource getFile(UUID uuid) {
        String fileName = fileRepository.getFileName(uuid);
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        return new FileSystemResource(filePath);
    }
}
