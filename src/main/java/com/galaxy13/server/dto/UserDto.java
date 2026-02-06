package com.galaxy13.server.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;

    private String username;

    private String email;

    private String role;

    private Instant createdAt;

    private Instant lastLogin;

    private Boolean isActive;

    private Long totalSaves;

    private Long totalStorage;
}
