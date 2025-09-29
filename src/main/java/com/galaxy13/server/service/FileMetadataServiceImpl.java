package com.galaxy13.server.service;

import com.galaxy13.server.dto.FileMetadataDto;
import com.galaxy13.server.dto.upsert.FileMetadataUpsertDto;

import java.time.Instant;
import java.util.UUID;

public class FileMetadataServiceImpl implements FileMetadataService {
    @Override
    public FileMetadataDto findById(Long id) {
        return null;
    }

    @Override
    public FileMetadataDto save(FileMetadataUpsertDto fileMetadata) {
        return null;
    }

    @Override
    public FileMetadataDto update(FileMetadataUpsertDto fileMetadata) {
        return null;
    }

    @Override
    public Instant lastModifiedDate(UUID id) {
        return null;
    }
}
