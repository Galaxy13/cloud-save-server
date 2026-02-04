package com.galaxy13.server.converter;

import com.galaxy13.server.dto.GameInfoDto;
import org.springframework.core.convert.converter.Converter;

public class GameInfoConverter implements Converter<GameInfo, GameInfoDto> {
    @Override
    public GameInfoDto convert(GameInfo source) {
        return null;
    }
}
