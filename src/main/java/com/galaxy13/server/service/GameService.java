package com.galaxy13.server.service;

import com.galaxy13.server.dto.GameDto;
import com.galaxy13.server.dto.GameSaveDto;
import com.galaxy13.server.exception.BadRequestException;
import com.galaxy13.server.exception.ResourceNotFoundException;
import com.galaxy13.server.model.Game;
import com.galaxy13.server.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;

    private final ConversionService conversionService;

    @Transactional(readOnly = true)
    public Page<GameDto> getAllGames(Pageable pageable) {
        return gameRepository.findByIsActiveTrue(pageable)
                .map(c -> conversionService.convert(c, GameDto.class));
    }

    @Transactional(readOnly = true)
    public Page<GameDto> searchGames(String search, Pageable pageable) {
        return gameRepository.searchGames(search, pageable)
                .map(c -> conversionService.convert(c, GameDto.class));
    }

    @Transactional(readOnly = true)
    public GameDto getGame(UUID id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        return conversionService.convert(game, GameDto.class);
    }

    @Transactional(readOnly = true)
    public GameDto getGameBySlug(String slug) {
        Game game = gameRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        return conversionService.convert(game, GameDto.class);
    }

    @Transactional
    public GameDto createGame(GameDto.CreateRequest request) {
        if (gameRepository.existsBySlug(request.getSlug())) {
            throw new BadRequestException("Game already exists");
        }
        Game game = Game.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .isActive(true)
                .build();
        game = gameRepository.save(game);
        log.info("Created Game {}", game.getSlug());
        return conversionService.convert(game, GameDto.class);
    }

    @Transactional
    public GameDto updateGame(UUID id, GameDto.UpdateRequest request) {
        Game game =  gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        if (request.getName() != null) {
            game.setName(request.getName());
        }
        if (request.getDescription() != null) {
            game.setDescription(request.getDescription());
        }
        if (request.getIconUrl() != null) {
            game.setIconUrl(request.getIconUrl());
        }
        if (request.getIsActive() != null) {
            game.setActive(request.getIsActive());
        }
        game = gameRepository.save(game);
        log.info("Updated Game {}", game.getSlug());
        return conversionService.convert(game, GameDto.class);
    }

    @Transactional
    public void deleteGame(UUID id) {
        Game game =  gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        game.setActive(false);
        gameRepository.save(game);
        log.info("Soft deleted Game {}", game.getSlug());
    }
}
