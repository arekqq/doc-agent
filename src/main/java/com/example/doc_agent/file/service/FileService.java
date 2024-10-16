package com.example.doc_agent.file.service;

import com.example.doc_agent.ai.service.AiDocService;
import com.example.doc_agent.file.dto.UploadFileStatus;
import com.example.doc_agent.file.dto.UploadResult;
import com.example.doc_agent.file.enums.FileExtension;
import com.example.doc_agent.file.exception.FileNotFoundException;
import com.example.doc_agent.file.persistence.FileRepository;
import com.example.doc_agent.file.util.FileValidator;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final AiDocService aiDocService;
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${file.upload-dir:/app/uploads}")
    private String uploadDir;

    public FileService(FileRepository fileRepository, AiDocService aiDocService) {
        this.fileRepository = fileRepository;
        this.aiDocService = aiDocService;
    }

    public UploadResult save(MultipartFile file) {
        FileValidator.validate(file);
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        UUID id = null;
        UploadFileStatus status;
        try {
            file.transferTo(new File(directory, file.getOriginalFilename()));
            logger.info("File uploaded successfully: {}", file.getOriginalFilename());
            id = fileRepository.add(file.getOriginalFilename());
            status = UploadFileStatus.SUCCESS;
            ingest(id);
        } catch (IOException e) {
            status = UploadFileStatus.FAILURE;
            logger.info("File upload failed: {}", file.getOriginalFilename());
        }
        return new UploadResult(status, Objects.toString(id));
    }

    private void ingest(UUID id) {
        String filePath = getFilePath(id);
        DocumentParser documentParser = FileExtension.fromFileName(filePath).getParser();
        Document document = loadDocument(filePath, documentParser);
        document.metadata().put("id", id);
        aiDocService.ingest(document);
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

    public void checkUploadedAlready(UUID uuid) {
        if (!fileRepository.exists(uuid)) {
            throw new FileNotFoundException();
        }
    }
}
