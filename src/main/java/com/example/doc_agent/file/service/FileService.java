package com.example.doc_agent.file.service;

import com.example.doc_agent.file.persistence.FileRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public UUID upload(MultipartFile file) throws IOException { // TODO handle all exceptions gracefully (dedicated exception and advisor with http code
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        }; // TODO move to some reader utility
        return fileRepository.add(resource);
    }

    public Resource getFile(UUID id) {
        return fileRepository.get(id);
    }
}
