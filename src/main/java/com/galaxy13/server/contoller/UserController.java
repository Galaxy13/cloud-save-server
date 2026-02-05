package com.galaxy13.server.contoller;

import com.galaxy13.server.dto.ApiResponse;
import com.galaxy13.server.dto.UserDto;
import com.galaxy13.server.exception.BadRequestException;
import com.galaxy13.server.exception.ResourceNotFoundException;
import com.galaxy13.server.model.User;
import com.galaxy13.server.repository.UserRepository;
import com.galaxy13.server.security.UserPrincipal;
import com.galaxy13.server.service.GameSaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Tag(name = "User", description = "Current user profile endpoints")
public class UserController {

    private final UserRepository userRepository;

    private final ConversionService conversionService;

    private final GameSaveService gameSaveService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserDto dto = conversionService.convert(user, UserDto.class);
        dto.setTotalSaves(gameSaveService.getUserSaveCount(user.getId()));
        dto.setTotalStorage(gameSaveService.getUserTotalStorage(user.getId()));

        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PatchMapping
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (request.email != null && !request.email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email)) {
                throw new BadRequestException("Email already exists");
            }
            user.setEmail(request.email);
        }
        user = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(conversionService.convert(user, UserDto.class),
                "Profile updated"));
    }

    @PostMapping("/password")
    @Operation(summary = "Change password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @Data
    public static class UpdateProfileRequest {
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
