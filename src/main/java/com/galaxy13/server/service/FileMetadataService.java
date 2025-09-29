package com.galaxy13.server.service;

import com.galaxy13.server.dto.FileMetadataDto;
import com.galaxy13.server.dto.upsert.FileMetadataUpsertDto;

import java.time.Instant;
import java.util.UUID;

public interface FileMetadataService {

    FileMetadataDto findById(Long id);

    FileMetadataDto save(FileMetadataUpsertDto fileMetadata);

    FileMetadataDto update(FileMetadataUpsertDto fileMetadata);

    Instant lastModifiedDate(UUID id);
}
