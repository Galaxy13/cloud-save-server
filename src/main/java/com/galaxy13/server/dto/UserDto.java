package com.galaxy13.server.dto;

import java.time.Instant;

import com.galaxy13.server.contoller.UserController;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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

    @Data
    public static class UserUpdateRequest {
        @Email(message = "Invalid email format")
        private String email;
    }

    @Data
    public static class ChangePasswordRequest {
        private String currentPassword;

        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;
    }
}
