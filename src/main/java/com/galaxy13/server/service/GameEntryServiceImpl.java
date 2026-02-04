package com.galaxy13.server.service;

import com.galaxy13.server.converter.GameEntryConverter;
import com.galaxy13.server.dto.FileMetadataDto;
import com.galaxy13.server.dto.GameEntryDto;
import com.galaxy13.server.dto.upsert.GameEntryUpsertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameEntryServiceImpl implements GameEntryService {

    private final GameEntryRepository gameEntryRepository;

    private final GameEntryConverter gameEntryConverter;

    @Transactional(readOnly = true)
    @Override
    public GameEntryDto findById(UUID id) {
        return gameEntryRepository.findById(id).map(gameEntryConverter::convert).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public GameEntryDto findByInfoId(UUID infoId) {
        return gameEntryRepository.findByGameInfoId(infoId)
                .map(gameEntryConverter::convert).orElse(null);
    }

    @Transactional
    @Override
    public GameEntryDto save(GameEntryUpsertDto gameEntryDto) {
        return null;
    }

    @Transactional
    @Override
    public GameEntryDto update(GameEntryUpsertDto gameEntryDto) {
        return null;
    }

    @Transactional
    @Override
    public Instant lastModified(UUID id) {
        return null;
    }

    @Transactional
    @Override
    public Set<FileMetadataDto> getEntryFiles(UUID id) {
        return Set.of();
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {

    }
}
