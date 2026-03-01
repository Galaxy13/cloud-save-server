package com.galaxy13.server.contoller;

import com.galaxy13.server.dto.ApiResponse;
import com.galaxy13.server.dto.GameDto;
import com.galaxy13.server.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Tag(name = "Games", description = "Game management endpoints")
public class GameController {

    private final GameService gameService;

    @GetMapping
    @Operation(summary = "Get all active games")
    public ResponseEntity<ApiResponse<ApiResponse.PagedResponse<GameDto>>> getAllGames(
            @PageableDefault(size = 50) Pageable pageable) {

        Page<GameDto> games = gameService.getAllGames(pageable);
        return ResponseEntity.ok(ApiResponse.success(ApiResponse.PagedResponse.from(games)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search games by name or slug")
    public ResponseEntity<ApiResponse<ApiResponse.PagedResponse<GameDto>>> searchGames(
            @RequestParam String q, @PageableDefault(size = 20) Pageable pageable) {
        Page<GameDto> games = gameService.searchGames(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(ApiResponse.PagedResponse.from(games)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get game by ID")
    public ResponseEntity<ApiResponse<GameDto>> getGameById(@PathVariable UUID id) {
        GameDto game = gameService.getGame(id);
        return ResponseEntity.ok(ApiResponse.success(game));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get game by slug")
    public ResponseEntity<ApiResponse<GameDto>> getGameBySlug(@PathVariable String slug) {
        GameDto game = gameService.getGameBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(game));
    }
}
