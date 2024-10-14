package com.example.doc_agent.file.web.controller;

import com.example.doc_agent.file.dto.UploadResult;
import com.example.doc_agent.file.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UploadResult> uploadFile(
        @RequestParam("file") MultipartFile file,
        HttpServletRequest request
    ) throws IOException {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file provided");
        }

        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (!"txt".equalsIgnoreCase(fileExtension) && !"pdf".equalsIgnoreCase(fileExtension)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only .txt or .pdf files are allowed");
        }

        UUID uuid = fileService.upload(file);
        return ResponseEntity.status(HttpStatus.CREATED)
            .location(URI.create("/file/" + uuid.toString())) // TODO fix to ad host
            .body(new UploadResult(uuid.toString()));
    }

    @GetMapping(value = "/file/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getFile(@PathVariable UUID id) throws IOException {
        Resource resource = fileService.getFile(id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
}
