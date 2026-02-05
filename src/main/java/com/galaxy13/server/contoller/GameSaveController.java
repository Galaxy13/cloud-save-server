package com.galaxy13.server.contoller;

import com.galaxy13.server.dto.ApiResponse;
import com.galaxy13.server.dto.GameSaveDto;
import com.galaxy13.server.model.SaveHistory;
import com.galaxy13.server.security.UserPrincipal;
import com.galaxy13.server.service.GameSaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/saves")
@RequiredArgsConstructor
@Tag(name = "Game Saves", description = "Game save management endpoints")
public class GameSaveController {

    private final GameSaveService gameSaveService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a new game save")
    public ResponseEntity<ApiResponse<GameSaveDto>> uploadSave(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam("file")MultipartFile file,
            @RequestParam("gameSlug") String gameSlug,
            @RequestParam("saveName") String saveName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isAutoSave", defaultValue = "false") Boolean isAutoSave) throws Exception {
        GameSaveDto.UploadRequest request = GameSaveDto.UploadRequest.builder()
                .gameSlug(gameSlug)
                .saveName(saveName)
                .description(description)
                .isAutoSave(isAutoSave)
                .build();
        GameSaveDto saveDto = gameSaveService.uploadSave(user.getId(), file, request);
        return ResponseEntity.ok(ApiResponse.success(saveDto, "Save uploaded successfully"));
    }

    @GetMapping
    @Operation(summary = "Get user's saves with pagination")
    public ResponseEntity<ApiResponse<ApiResponse.PagedResponse<GameSaveDto>>> getUserSaves(
            @AuthenticationPrincipal UserPrincipal user,
            @PageableDefault(size = 20, sort = "updatedAt") Pageable pageable){
        Page<GameSaveDto> saves = gameSaveService.getUserSaves(user.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(ApiResponse.PagedResponse.from(saves)));
    }

    @GetMapping("/game/{gameSlug}")
    @Operation(summary = "Get user's saves for a specific game")
    public ResponseEntity<ApiResponse<List<GameSaveDto>>> getSavesByGame(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String gameSlug) {
        List<GameSaveDto> saves = gameSaveService.getUserSavesByGameSlug(user.getId(), gameSlug);
        return ResponseEntity.ok(ApiResponse.success(saves));
    }

    @GetMapping("/{saveId}")
    @Operation(summary = "Get a specific save with download URL")
    public ResponseEntity<ApiResponse<GameSaveDto>> getSave(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID saveId) throws Exception {
        GameSaveDto saveDto = gameSaveService.getSaveWithDownloadUrl(user.getId(), saveId);
        return ResponseEntity.ok(ApiResponse.success(saveDto));
    }

    @GetMapping("/{saveId}/download")
    @Operation(summary = "Download a save file directly")
    public ResponseEntity<InputStreamResource> downloadSave(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID saveId) throws Exception {
        GameSaveDto save =  gameSaveService.getSave(user.getId(), saveId);
        InputStream inputStream = gameSaveService.downloadSave(user.getId(), saveId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + save.getSaveName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(save.getFileSize())
                .body(new InputStreamResource(inputStream));
    }

    @PutMapping(value = "/{saveId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a save (optionally with new file)")
    public ResponseEntity<ApiResponse<GameSaveDto>> updateSave(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID saveId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "saveName", required = false) String saveName,
            @RequestParam(value = "description", required = false) String description) throws Exception {

        GameSaveDto.UpdateRequest request = GameSaveDto.UpdateRequest.builder()
                .saveName(saveName)
                .description(description)
                .build();
        GameSaveDto save = gameSaveService.updateSave(user.getId(), saveId, file, request);
        return ResponseEntity.ok(ApiResponse.success(save, "Save updated successfully"));
    }

    @DeleteMapping("/{saveId}")
    @Operation(summary = "Delete a save")
    public ResponseEntity<ApiResponse<Void>> deleteSave(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID saveId) throws Exception {

        gameSaveService.deleteSave(user.getId(), saveId);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete save successfully"));
    }

    @GetMapping("/{saveId}/history")
    @Operation(summary = "Get version history of a save")
    public ResponseEntity<ApiResponse<List<SaveHistory>>> getSaveHistory(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID saveId) {
        List<SaveHistory> history = gameSaveService.getSaveHistory(user.getId(), saveId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/{saveId}/history/{historyId}/download}")
    @Operation(summary = "Download a historical version of a save")
    public ResponseEntity<InputStreamResource> downloadHistoryVersion(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID saveId,
            @PathVariable UUID historyId) throws Exception {

        InputStream inputStream = gameSaveService.downloadHistoryVersion(user.getId(), saveId, historyId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }
}
