package com.galaxy13.server.converter;

import com.galaxy13.server.dto.FileMetadataDto;
import org.springframework.core.convert.converter.Converter;

public class FileMetadataConverter implements Converter<FileMetadata, FileMetadataDto> {
    @Override
    public FileMetadataDto convert(FileMetadata source) {
        return null;
    }
}
