package com.galaxy13.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {

    private String id;

    private String name;

    private String slug;

    private String description;

    private String iconUrl;

    private Instant createdAt;

    private Boolean isActive;

    private Long saveCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Game name is required")
        @Size(max = 255, message = "Game name must not exceed 255 characters")
        private String name;

        @NotBlank(message = "Slug is required")
        @Size(max = 100, message = "Slug must not exceed 100 characters")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
        private String slug;

        private String description;

        private String iconUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @Size(max = 255, message = "Game name must not exceed 255 characters")
        private String name;

        private String description;

        private String iconUrl;

        private Boolean isActive;
    }
}
