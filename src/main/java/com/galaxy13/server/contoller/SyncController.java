package com.galaxy13.server.contoller;

import com.galaxy13.server.dto.ApiResponse;
import com.galaxy13.server.dto.GameSaveDto;
import com.galaxy13.server.security.UserPrincipal;
import com.galaxy13.server.service.GameSaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
@Tag(name = "Sync", description = "Client synchronization endpoints")
public class SyncController {

    private final GameSaveService gameSaveService;

    @PostMapping("/check")
    @Operation(summary = "Check if local save needs sync with server")
    public ResponseEntity<ApiResponse<GameSaveDto.SyncResponse>> checkSync(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody GameSaveDto.SyncRequest request) {

        GameSaveDto.SyncResponse response = gameSaveService.checkSync(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status")
    @Operation(summary = "Get user's sync status summary")
    public ResponseEntity<ApiResponse<SyncStatus>> getSyncStatus(
            @AuthenticationPrincipal UserPrincipal user) {

        long saveCount = gameSaveService.getUserSaveCount(user.getId());
        Long totalStorage = gameSaveService.getUserTotalStorage(user.getId());

        SyncStatus syncStatus = new SyncStatus(saveCount, totalStorage);
        return ResponseEntity.ok(ApiResponse.success(syncStatus));
    }

    public record SyncStatus(long saveCount, Long totalStorage) {}
}
