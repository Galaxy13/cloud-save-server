package com.galaxy13.server.service;

import com.galaxy13.server.dto.GameInfoDto;

import java.util.Set;

public class GameInfoServiceImpl implements GameInfoService {
    @Override
    public Set<GameInfoDto> findByName(String name) {
        return Set.of();
    }

    @Override
    public GameInfoDto findById(long id) {
        return null;
    }

    @Override
    public GameInfoDto save(GameInfoDto gameInfoDto) {
        return null;
    }

    @Override
    public GameInfoDto update(GameInfoDto gameInfoDto) {
        return null;
    }

    @Override
    public void deleteById(long id) {

    }
}
