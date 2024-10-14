package com.example.doc_agent.file.persistence;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class FileRepository {

    private final Map<UUID, Resource> repository = new HashMap<>();

    public UUID add(Resource resource) {
        UUID uuid = UUID.randomUUID();
        repository.put(uuid, resource);
        return uuid;
    }

    public Resource get(UUID uuid) {
        return repository.get(uuid);
    }
}
