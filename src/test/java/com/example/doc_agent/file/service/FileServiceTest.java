package com.example.doc_agent.file.service;

import com.example.doc_agent.ai.service.AiDocService;
import com.example.doc_agent.file.dto.UploadFileStatus;
import com.example.doc_agent.file.dto.UploadResult;
import com.example.doc_agent.file.exception.FileNotFoundException;
import com.example.doc_agent.file.persistence.FileRepository;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private AiDocService aiDocService;

    @InjectMocks
    private FileService fileService;

    private String uploadDir;

    @BeforeAll
    static void beforeAll() {
        mockStatic(FileSystemDocumentLoader.class);
    }


    @BeforeEach
    void setUp() throws IOException {
        fileService = new FileService(fileRepository, aiDocService);
        uploadDir = Files.createTempDirectory("test-uploads").toString();
        ReflectionTestUtils.setField(fileService, "uploadDir", uploadDir);
    }

    @Test
    void save_ShouldReturnSuccess_WhenFileIsValid() {
        // Given
        String fileName = "test-file.txt";
        MockMultipartFile mockFile = new MockMultipartFile(
            "file", fileName, "text/plain", "Test content".getBytes());
        UUID mockId = UUID.randomUUID();
        when(fileRepository.add(anyString())).thenReturn(mockId);
        when(fileRepository.getFileName(any())).thenReturn(fileName);
        when(FileSystemDocumentLoader.loadDocument(anyString(), any())).thenReturn(null);

        // When
        UploadResult result = fileService.save(mockFile);

        //Then
        assertEquals(UploadFileStatus.SUCCESS, result.status());
        assertEquals(mockId.toString(), result.id());
        verify(aiDocService, times(1)).ingest(any());
        verify(fileRepository, times(1)).add(mockFile.getOriginalFilename());
        File directory = new File(uploadDir);
        assertTrue(directory.exists());
    }

    @Test
    void save_ShouldReturnFailure_WhenFileSaveFails() throws Exception {
        // Given
        String fileName = "testfile.txt";
        MockMultipartFile mockFile = new MockMultipartFile(
            "file", fileName, "text/plain", "Test content".getBytes());
        when(fileRepository.getFileName(any())).thenReturn(fileName);
        MockMultipartFile spyFile = spy(mockFile);
        doThrow(IOException.class).when(spyFile).transferTo(any(File.class));

        // When
        UploadResult result = fileService.save(spyFile);

        // Then
        assertEquals(UploadFileStatus.FAILURE, result.status());
        verify(fileRepository, never()).add(anyString());
    }

    @Test
    void getFilePath_ShouldReturnFilePath_WhenFileExists() {
        // Given
        UUID mockId = UUID.randomUUID();
        String mockFileName = "testfile.txt";
        when(fileRepository.getFileName(mockId)).thenReturn(mockFileName);

        // When
        String filePath = fileService.getFilePath(mockId);

        // Then
        assertEquals(uploadDir + "/" + mockFileName, filePath);
    }

    @Test
    void getFilePath_ShouldThrowException_WhenFileNotFound() {
        // Given
        UUID mockId = UUID.randomUUID();
        when(fileRepository.getFileName(mockId)).thenReturn(null);

        // When + Then
        assertThrows(ResponseStatusException.class, () -> fileService.getFilePath(mockId));
    }

    @Test
    void getFile_ShouldReturnResource_WhenFileExists() {
        // Given
        UUID mockId = UUID.randomUUID();
        String mockFileName = "testfile.txt";
        when(fileRepository.getFileName(mockId)).thenReturn(mockFileName);

        // When
        Resource resource = fileService.getFile(mockId);

        // Then
        assertNotNull(resource);
        assertEquals(mockFileName, resource.getFilename());
        assertInstanceOf(FileSystemResource.class, resource);
    }

    @Test
    void checkUploadedAlready_ShouldThrowException_WhenFileDoesNotExist() {
        // Given
        UUID mockId = UUID.randomUUID();
        when(fileRepository.exists(mockId)).thenReturn(false);

        // When + Then
        assertThrows(FileNotFoundException.class, () -> fileService.checkUploadedAlready(mockId));
    }

    @Test
    void checkUploadedAlready_ShouldNotThrowException_WhenFileExists() {
        // Given
        UUID mockId = UUID.randomUUID();
        when(fileRepository.exists(mockId)).thenReturn(true);

        // When + Then
        assertDoesNotThrow(() -> fileService.checkUploadedAlready(mockId));
    }
}
