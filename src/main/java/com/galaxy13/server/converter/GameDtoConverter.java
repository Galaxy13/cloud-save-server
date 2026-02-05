package com.galaxy13.server.converter;

import com.galaxy13.server.dto.GameDto;
import com.galaxy13.server.model.Game;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GameDtoConverter implements Converter<Game,  GameDto> {
    @Override
    public GameDto convert(Game source) {
        return GameDto.builder()
                .id(source.getId().toString())
                .name(source.getName())
                .slug(source.getSlug())
                .description(source.getDescription())
                .iconUrl(source.getIconUrl())
                .createdAt(source.getCreatedAt())
                .isActive(source.isActive())
                .build();
    }
}
