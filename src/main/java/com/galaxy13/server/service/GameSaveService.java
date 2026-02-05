package com.galaxy13.server.service;

import com.galaxy13.server.dto.GameSaveDto;
import com.galaxy13.server.exception.ResourceNotFoundException;
import com.galaxy13.server.model.Game;
import com.galaxy13.server.model.GameSave;
import com.galaxy13.server.model.SaveHistory;
import com.galaxy13.server.model.User;
import com.galaxy13.server.repository.GameRepository;
import com.galaxy13.server.repository.GameSaveRepository;
import com.galaxy13.server.repository.SaveHistoryRepository;
import com.galaxy13.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSaveService {

    private final GameSaveRepository gameSaveRepository;

    private final GameRepository gameRepository;

    private final UserRepository userRepository;

    private final SaveHistoryRepository saveHistoryRepository;

    private final FileStorageService fileStorageService;

    private final ConversionService conversionService;

    @Transactional
    public GameSaveDto uploadSave(UUID userId, MultipartFile file, GameSaveDto.UploadRequest request) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Game game = gameRepository.findBySlug(request.getGameSlug())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        String checksum = fileStorageService.calculateChecksum(file);

        Optional<GameSave> existingSave = gameSaveRepository.findByChecksumAndUserId(checksum, userId);
        if (existingSave.isPresent() && existingSave.get().getGame().getId().equals(game.getId())) {
            log.info("Duplicate save detected for user {} and game {}",userId, game.getId());
            return conversionService.convert(existingSave.get(), GameSaveDto.class);
        }
        String fileKey = fileStorageService.uploadFile(file, userId, game.getSlug());

        GameSave gameSave = GameSave.builder()
                .user(user)
                .game(game)
                .saveName(request.getSaveName())
                .description(request.getDescription())
                .fileKey(fileKey)
                .size(file.getSize())
                .checksum(checksum)
                .metadata(request.getMetadata())
                .isAutoSave(request.getIsAutoSave() != null ? request.getIsAutoSave() : false)
                .version(1)
                .build();

        gameSave = gameSaveRepository.save(gameSave);
        log.info("Save {} has been successfully uploaded for user {}", gameSave.getId(), userId);
        return conversionService.convert(gameSave, GameSaveDto.class);
    }

    @Transactional
    public GameSaveDto updateSave(UUID userId, UUID saveId,
                                  MultipartFile file, GameSaveDto.UpdateRequest request) throws Exception {
        GameSave gameSave = gameSaveRepository.findByIdAndUserId(saveId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Save not found"));

        if (file != null && !file.isEmpty()) {
            String checksum = fileStorageService.calculateChecksum(file);

            if (!checksum.equals(gameSave.getChecksum())) {
                SaveHistory history = SaveHistory.builder()
                        .gameSave(gameSave)
                        .fileKey(gameSave.getFileKey())
                        .size(gameSave.getSize())
                        .checksum(gameSave.getChecksum())
                        .version(gameSave.getVersion())
                        .build();
                saveHistoryRepository.save(history);

                String newFileKey = fileStorageService.uploadFile(file, userId, gameSave.getFileKey());

                gameSave.setFileKey(newFileKey);
                gameSave.setSize(file.getSize());
                gameSave.setChecksum(checksum);
                gameSave.setVersion(gameSave.getVersion() + 1);
            }
        }

        if (request.getSaveName() != null) {
            gameSave.setSaveName(request.getSaveName());
        }
        if (request.getDescription() != null) {
            gameSave.setDescription(request.getDescription());
        }
        if (request.getMetadata() != null) {
            gameSave.setMetadata(request.getMetadata());
        }
        gameSave = gameSaveRepository.save(gameSave);
        return conversionService.convert(gameSave, GameSaveDto.class);
    }

    @Transactional(readOnly = true)
    public Page<GameSaveDto> getUserSaves(UUID userId, Pageable pageable) {
        return gameSaveRepository.findByUserId(userId, pageable)
                .map(gameSave -> conversionService.convert(gameSave, GameSaveDto.class));
    }

    @Transactional(readOnly = true)
    public Page<GameSaveDto> getUserSavesByGame(UUID userId, UUID gameId, Pageable pageable) {
        return gameSaveRepository.findByUserIdAndGameId(userId, gameId, pageable)
                .map(gameSave -> conversionService.convert(gameSave, GameSaveDto.class));
    }

    @Transactional(readOnly = true)
    public List<GameSaveDto> getUserSavesByGameSlug(UUID userId, String gameSlug) {
        return gameSaveRepository.findByUserIdAndGameSlug(userId, gameSlug).stream()
                .map(gameSave -> conversionService.convert(gameSave, GameSaveDto.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public GameSaveDto getSave(UUID userId, UUID saveId) {
        GameSave gameSave = gameSaveRepository.findByIdAndUserId(saveId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Save not found"));
        return conversionService.convert(gameSave, GameSaveDto.class);
    }

    @Transactional(readOnly = true)
    public GameSaveDto getSaveWithDownloadUrl(UUID userId, UUID saveId) throws Exception {
        GameSave gameSave = gameSaveRepository.findByIdAndUserId(saveId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Save not found"));
        GameSaveDto gameSaveDto = conversionService.convert(gameSave, GameSaveDto.class);
        gameSaveDto.setDownloadUrl(fileStorageService.getPresignedDownloadUrl(gameSave.getFileKey(), 60));
        return gameSaveDto;
    }

    public InputStream downloadSave(UUID userId, UUID saveId) throws Exception {
        GameSave gameSave = gameSaveRepository.findByIdAndUserId(saveId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Save not found"));
        return fileStorageService.downloadFile(gameSave.getFileKey());
    }

    @Transactional
    public void deleteSave(UUID userId, UUID saveId) throws Exception {
        GameSave gameSave = gameSaveRepository.findByIdAndUserId(saveId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Save not found"));

        List<SaveHistory> history = saveHistoryRepository.findByGameSaveIdOrderByVersionDesc(gameSave.getId());
        for (SaveHistory saveHistory : history) {
            try {
                fileStorageService.deleteFile(saveHistory.getFileKey());
            } catch (Exception e) {
                log.warn("Delete save failed with key {}", saveHistory.getFileKey());
            }
        }
        fileStorageService.deleteFile(gameSave.getFileKey());
        gameSaveRepository.delete(gameSave);
        log.info("Save {} for user {} has been successfully deleted", saveId, userId);
    }

    public GameSaveDto.SyncResponse checkSync(UUID userId, GameSaveDto.SyncRequest request) {
        List<GameSave> saves = gameSaveRepository.findByUserIdAndGameSlug(userId, request.getGameSlug());

        if (saves.isEmpty()) {
            if (request.getLastKnownChecksum() != null) {
                return GameSaveDto.SyncResponse.builder()
                        .needsSync(true)
                        .action("UPLOAD")
                        .build();
            }
            return GameSaveDto.SyncResponse.builder()
                    .needsSync(false)
                    .action("NONE")
                    .build();
        }
        GameSave latestSave = saves.stream()
                .max(Comparator.comparing(GameSave::getUpdatedAt))
                .orElse(null);

        if (latestSave == null) {
            return GameSaveDto.SyncResponse.builder()
                    .needsSync(false)
                    .action("NONE")
                    .build();
        }

        if (request.getLastKnownChecksum() == null) {
            return GameSaveDto.SyncResponse.builder()
                    .needsSync(true)
                    .action("DOWNLOAD")
                    .serverSave(conversionService.convert(latestSave, GameSaveDto.class))
                    .build();
        }
        if (request.getLastKnownChecksum().equals(latestSave.getChecksum())) {
            return GameSaveDto.SyncResponse.builder()
                    .needsSync(false)
                    .action("NONE")
                    .build();
        }
        if (request.getLastSyncTime() != null && latestSave.getUpdatedAt().isAfter(request.getLastSyncTime())) {
            return GameSaveDto.SyncResponse.builder()
                    .needsSync(true)
                    .action("CONFLICT")
                    .serverSave(conversionService.convert(latestSave, GameSaveDto.class))
                    .conflictReason("Server has newer save than last sync time")
                    .build();
        }
        return GameSaveDto.SyncResponse.builder()
                .needsSync(true)
                .action("UPLOAD")
                .serverSave(conversionService.convert(latestSave, GameSaveDto.class))
                .build();
    }

    public List<SaveHistory> getSaveHistory(UUID userId, UUID saveId) {
        GameSave gameSave = gameSaveRepository.findByIdAndUserId(saveId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Save not found"));
        return saveHistoryRepository.findByGameSaveIdOrderByVersionDesc(gameSave.getId());
    }

    public InputStream downloadHistoryVersion(UUID userId, UUID saveId, UUID historyId) throws Exception {
        GameSave gameSave = gameSaveRepository.findByIdAndUserId(saveId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Save not found"));

        SaveHistory history = saveHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("History version not found"));
        if (!history.getGameSave().getId().equals(gameSave.getId())) {
            throw new ResourceNotFoundException("History version does not belong to this save");
        }
        return fileStorageService.downloadFile(history.getFileKey());
    }

    public long getUserSaveCount(UUID userId) {
        return gameSaveRepository.countByUserId(userId);
    }

    public Long getUserTotalStorage(UUID userId) {
        Long total = gameSaveRepository.getTotalFileSizeByUserId(userId);
        return total != null ? total : 0L;
    }
}

