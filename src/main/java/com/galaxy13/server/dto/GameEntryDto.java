package com.galaxy13.server.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record GameEntryDto(UUID id,
                           String title,
                           UUID gameInfo,
                           Instant lastModified,
                           Set<FileMetadataDto> filesMetadata,
                           String version) {
}
