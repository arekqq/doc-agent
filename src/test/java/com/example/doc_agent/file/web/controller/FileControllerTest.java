package com.example.doc_agent.file.web.controller;

import com.example.doc_agent.file.dto.UploadFileStatus;
import com.example.doc_agent.file.dto.UploadResult;
import com.example.doc_agent.file.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class FileControllerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }


    @Test
    void uploadFile_ShouldReturnCreated() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.txt", "text/plain", "Test content".getBytes());
        UploadResult mockResult = new UploadResult(UploadFileStatus.SUCCESS, "file-id-123");
        when(fileService.save(any(MultipartFile.class))).thenReturn(mockResult);

        // When + Then
        mockMvc.perform(multipart("/upload")
                .file(mockFile))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/file/file-id-123"))
            .andExpect(jsonPath("$.id").value("file-id-123"))
            .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void uploadFile_ShouldReturnInternalServerError() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.txt", "text/plain", "Test content".getBytes());
        UploadResult mockResult = new UploadResult(UploadFileStatus.FAILURE, "file-id-123");
        when(fileService.save(any(MultipartFile.class))).thenReturn(mockResult);

        // When + Then
        mockMvc.perform(multipart("/upload")
                .file(mockFile))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.id").value("file-id-123"))
            .andExpect(jsonPath("$.status").value("FAILURE"));
    }

    @Test
    void getFile_ShouldReturnFile() throws Exception {
        // Given
        Resource mockResource = new ByteArrayResource("Test content".getBytes()) {
            @Override
            public String getFilename() {
                return "testfile.txt";
            }
        };
        when(fileService.getFile(any(UUID.class))).thenReturn(mockResource);

        // When + Then
        mockMvc.perform(get("/file/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"testfile.txt\""))
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(content().bytes("Test content".getBytes()));
    }
}
