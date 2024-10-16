package com.example.doc_agent.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "File doesn't exist")
public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException() {
        super();
    }
}
