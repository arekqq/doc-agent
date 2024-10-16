package com.example.doc_agent.file.enums;

import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum FileExtension {
    TXT("txt", TextDocumentParser::new),
    PDF("pdf", ApachePdfBoxDocumentParser::new);

    private final String extension;
    private final Supplier<DocumentParser> parserProvider;
    private static final Set<FileExtension> SUPPORTED = Set.of(values());

    FileExtension(String extension, Supplier<DocumentParser> parserProvider) {
        this.extension = extension;
        this.parserProvider = parserProvider;
    }

    public static FileExtension fromFileName(String fileName) {
        String fileExtension = StringUtils.getFilenameExtension(fileName);
        return SUPPORTED.stream()
            .filter(a -> a.extension.equalsIgnoreCase(fileExtension))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported file extension: " + fileExtension));
    }

    public static Set<String> getSupportedFileExtensions() {
        return SUPPORTED.stream()
            .map(FileExtension::getExtension)
            .collect(Collectors.toSet());
    }

    public String getExtension() {
        return extension;
    }

    public DocumentParser getParser() {
        return parserProvider.get();
    }
}
