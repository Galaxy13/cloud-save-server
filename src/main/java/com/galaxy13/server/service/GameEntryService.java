package com.galaxy13.server.service;

import com.galaxy13.server.dto.FileMetadataDto;
import com.galaxy13.server.dto.GameEntryDto;
import com.galaxy13.server.dto.upsert.GameEntryUpsertDto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface GameEntryService {
    GameEntryDto findById(UUID id);

    GameEntryDto findByInfoId(UUID infoId);

    GameEntryDto save(GameEntryUpsertDto gameEntryDto);

    GameEntryDto update(GameEntryUpsertDto gameEntryDto);

    Instant lastModified(UUID id);

    Set<FileMetadataDto> getEntryFiles(UUID id);

    void deleteById(UUID id);
}
