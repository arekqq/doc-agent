package com.example.doc_agent.file.util;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

public class FileValidator {

    private FileValidator() {
    }

    private static final Set<String> SUPPORTED_FILE_EXTENSIONS = Set.of("txt", "pdf");

    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file provided");
        }

        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (fileExtension == null
            || !SUPPORTED_FILE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Currently only " + SUPPORTED_FILE_EXTENSIONS.toString() + " files are supported");
        }
    }
}
