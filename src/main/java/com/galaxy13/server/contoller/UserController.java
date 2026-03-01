package com.galaxy13.server.contoller;

import com.galaxy13.server.dto.ApiResponse;
import com.galaxy13.server.dto.UserDto;
import com.galaxy13.server.security.UserPrincipal;
import com.galaxy13.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Tag(name = "User", description = "Current user profile endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {
        UserDto dto = userService.getCurrentUser(principal.getId());

        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PatchMapping
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserDto.UserUpdateRequest request) {

        UserDto dto = userService.updateUser(principal.getId(), request);
        return ResponseEntity.ok(
                ApiResponse.success(dto, "Profile updated"));
    }

    @PostMapping("/password")
    @Operation(summary = "Change password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserDto.ChangePasswordRequest request) {

        userService.updatePassword(principal.getId(), request);

        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
}
