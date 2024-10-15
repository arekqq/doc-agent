package com.example.doc_agent.file.web.controller;

import com.example.doc_agent.file.dto.UploadResult;
import com.example.doc_agent.file.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public ResponseEntity<UploadResult> uploadFile(@RequestParam("file") MultipartFile file,
                                                   HttpServletRequest request) {
        UUID uuid = fileService.upload(file);
        return ResponseEntity.status(HttpStatus.CREATED)
            .location(buildLocationUri(request, uuid))
            .body(new UploadResult(uuid.toString()));
    }

    private URI buildLocationUri(HttpServletRequest request,
                                 UUID uuid) {
        return ServletUriComponentsBuilder
            .fromRequestUri(request)
            .replacePath("/file/" + uuid.toString())
            .build()
            .toUri();
    }

    @GetMapping(value = "/file/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getFile(@PathVariable UUID id) {
        Resource resource = fileService.getFile(id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
}
