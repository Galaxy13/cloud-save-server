package com.galaxy13.server.service;

import com.galaxy13.server.dto.GameInfoDto;

import java.util.Set;

public interface GameInfoService {
    Set<GameInfoDto> findByName(String name);

    GameInfoDto findById(long id);

    GameInfoDto save(GameInfoDto gameInfoDto);

    GameInfoDto update(GameInfoDto gameInfoDto);

    void deleteById(long id);
}
