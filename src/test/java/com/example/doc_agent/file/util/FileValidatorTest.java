package com.example.doc_agent.file.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileValidatorTest {
    @Test
    @DisplayName("Should throw exception when file is null")
    void testValidate_NullFile() {
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> FileValidator.validate(null),
            "Expected validate(null) to throw, but it didn't"
        );

        assertEquals("No file provided", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when file is empty")
    void testValidate_EmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "text/plain", new byte[0]);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> FileValidator.validate(emptyFile),
            "Expected validate(emptyFile) to throw, but it didn't"
        );

        assertEquals("No file provided", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when file extension is unsupported")
    void testValidate_UnsupportedFileExtension() {
        MockMultipartFile unsupportedFile = new MockMultipartFile("file", "document.jpg", "image/jpeg", "Test Content".getBytes());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> FileValidator.validate(unsupportedFile),
            "Expected validate(unsupportedFile) to throw, but it didn't"
        );

        assertEquals("Currently only [pdf, txt] files are supported", exception.getReason());
    }

    @Test
    @DisplayName("Should pass validation for supported .txt file")
    void testValidate_SupportedTxtFile() {
        MockMultipartFile txtFile = new MockMultipartFile("file", "document.txt", "text/plain", "Test Content".getBytes());

        // Should not throw an exception
        FileValidator.validate(txtFile);
    }

    @Test
    @DisplayName("Should pass validation for supported .pdf file")
    void testValidate_SupportedPdfFile() {
        MockMultipartFile pdfFile = new MockMultipartFile("file", "document.pdf", "application/pdf", "Test Content".getBytes());

        // Should not throw an exception
        FileValidator.validate(pdfFile);
    }
}
