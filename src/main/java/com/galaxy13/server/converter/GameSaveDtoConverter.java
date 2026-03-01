package com.galaxy13.server.converter;

import com.galaxy13.server.dto.GameSaveDto;
import com.galaxy13.server.model.GameSave;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GameSaveDtoConverter implements Converter<GameSave, GameSaveDto> {
    @Override
    public GameSaveDto convert(GameSave source) {
        return GameSaveDto.builder()
                .id(source.getId().toString())
                .userId(source.getUser().getId().toString())
                .gameId(source.getGame().getId().toString())
                .gameName(source.getGame().getName())
                .gameSlug(source.getGame().getSlug())
                .saveName(source.getSaveName())
                .description(source.getDescription())
                .fileSize(source.getSize())
                .checksum(source.getChecksum())
                .metadata(source.getMetadata())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .isAutoSave(source.isAutoSave())
                .version(source.getVersion())
                .build();
    }
}
