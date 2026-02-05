package com.galaxy13.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSaveDto {

    private String id;

    private String gameId;

    private String userId;

    private String gameName;

    private String gameSlug;

    private String saveName;

    private String description;

    private Long fileSize;

    private String checksum;

    private Map<String, Object> metadata;

    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isAutoSave;

    private Integer version;

    private String downloadUrl;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadRequest {
        @NotBlank(message = "Game slug is required")
        private String gameSlug;

        @NotBlank(message = "Save name is required")
        @Size(max = 255, message = "Save name must not exceed 255 characters")
        private String saveName;

        private String description;

        private Map<String, Object> metadata;

        private Boolean isAutoSave;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "Save name is required")
        @Size(max = 255, message = "Save name must not exceed 255 characters")
        private String saveName;

        private String description;

        private Map<String, Object> metadata;

        private Boolean isAutoSave;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncRequest {
        @NotBlank(message = "Game slug is required")
        private String gameSlug;

        private String lastKnownChecksum;

        private Instant lastSyncTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncResponse {
        private boolean needsSync;

        private String action;

        private GameSaveDto gameSaveDto;

        private String conflictReason;
    }
}
