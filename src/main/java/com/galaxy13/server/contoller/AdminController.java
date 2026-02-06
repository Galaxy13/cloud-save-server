package com.galaxy13.server.contoller;

import com.galaxy13.server.dto.ApiResponse;
import com.galaxy13.server.dto.GameDto;
import com.galaxy13.server.dto.UserDto;
import com.galaxy13.server.service.AdminService;
import com.galaxy13.server.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administration endpoints (Admin role required)")
public class AdminController {
    private final AdminService adminService;
    private final GameService gameService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        Map<String, Object> stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users with pagination")
    public ResponseEntity<ApiResponse<ApiResponse.PagedResponse<UserDto>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {

        Page<UserDto> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(ApiResponse.PagedResponse.from(users)));
    }

    @GetMapping("/users/search")
    @Operation(summary = "Search users")
    public ResponseEntity<ApiResponse<ApiResponse.PagedResponse<UserDto>>> searchUsers(
            @RequestParam String q, @PageableDefault(size = 20) Pageable pageable) {

        Page<UserDto> users = adminService.searchUsers(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(ApiResponse.PagedResponse.from(users)));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user details")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable UUID id) {
        UserDto user = adminService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PatchMapping("/users/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {

        UserDto user = adminService.updateUser(id, request.email, request.role, request.isActive);
        return ResponseEntity.ok(ApiResponse.success(user, "User updated"));
    }

    @PostMapping("/users/{id}/reset-password")
    @Operation(summary = "Reset user password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable UUID id, @Valid @RequestBody ResetPasswordRequest request) {

        adminService.resetUserPassword(id, request.newPassword);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted"));
    }

    @GetMapping("/games")
    @Operation(summary = "Get all games including inactive")
    public ResponseEntity<ApiResponse<ApiResponse.PagedResponse<GameDto>>> getAllGames(
            @PageableDefault(size = 50) Pageable pageable) {

        Page<GameDto> games = adminService.getAllGamesAdmin(pageable);
        return ResponseEntity.ok(ApiResponse.success(ApiResponse.PagedResponse.from(games)));
    }

    @PostMapping("/games")
    @Operation(summary = "Create a new game")
    public ResponseEntity<ApiResponse<GameDto>> createGame(
            @Valid @RequestBody GameDto.CreateRequest request) {
        GameDto game = gameService.createGame(request);
        return ResponseEntity.ok(ApiResponse.success(game, "Game created"));
    }

    @PatchMapping("/games/{id}")
    @Operation(summary = "Update a game")
    public ResponseEntity<ApiResponse<GameDto>> updateGame(
            @PathVariable UUID id, @Valid @RequestBody GameDto.UpdateRequest request) {

        GameDto game = gameService.updateGame(id, request);
        return ResponseEntity.ok(ApiResponse.success(game, "Game updated"));
    }

    @DeleteMapping("/games/{id}")
    @Operation(summary = "Delete a game (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteGame(@PathVariable UUID id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Game deleted"));
    }

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
