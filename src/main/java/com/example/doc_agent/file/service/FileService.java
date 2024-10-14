package com.example.doc_agent.file.service;

import com.example.doc_agent.file.persistence.FileRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public UUID upload(MultipartFile file) {
        // TODO migrate MultipartFile into Resource

        return fileRepository.add(new FileSystemResource(file.getOriginalFilename()));
    }
}
