package com.example.doc_agent.file.util;

import com.example.doc_agent.file.enums.FileExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

public class FileValidator {

    private FileValidator() {
    }

    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file provided");
        }

        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (fileExtension == null
            || !FileExtension.getSupportedFileExtensions().contains(fileExtension.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Currently only " + FileExtension.getSupportedFileExtensions().toString() + " files are supported");
        }
    }
}
