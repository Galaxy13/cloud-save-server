package com.galaxy13.server.converter;

import com.galaxy13.server.dto.GameEntryDto;
import com.galaxy13.server.model.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GameEntryConverter implements Converter<Game, GameEntryDto> {

    private final FileMetadataConverter fileMetadataConverter;

    @Override
    public GameEntryDto convert(Game source) {
        return new GameEntryDto(source.getId(),
                source.getName(),
                source.getGameInfo().getId(),
                source.getLastModified().toInstant(),
                source.getFiles().stream().map(fileMetadataConverter::convert).collect(Collectors.toSet()),
                source.getVersion());
    }
}
