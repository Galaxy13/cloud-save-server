package com.galaxy13.server.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminDto {

    @Data
    public static class UpdateUserRequest {
        private String email;
        private String role;
        private Boolean isActive;
    }

    @Data
    public static class ResetPasswordRequest {
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;
    }
}
