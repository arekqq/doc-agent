package com.example.doc_agent.file.service;

import com.example.doc_agent.file.persistence.FileRepository;
import com.example.doc_agent.file.service.util.ResourceReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public UUID upload(MultipartFile file) {
        validate(file);
        ByteArrayResource resource = ResourceReader.read(file);
        return fileRepository.add(resource);
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

    public Resource getFile(UUID id) {
        Resource resource = fileRepository.get(id);
        if (resource == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        return resource;
    }
}
