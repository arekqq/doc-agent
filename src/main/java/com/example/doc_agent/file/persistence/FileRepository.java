package com.example.doc_agent.file.persistence;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class FileRepository {

    private final Map<UUID, String> repository = new HashMap<>();

    public UUID add(String fileName) {
        UUID uuid = UUID.randomUUID();
        repository.put(uuid, fileName);
        return uuid;
    }

    public String getFileName(UUID uuid) {
        return repository.get(uuid);
    }
}
