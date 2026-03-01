package com.galaxy13.server.contoller;

import com.galaxy13.server.dto.ApiResponse;
import com.galaxy13.server.dto.AuthDto;
import com.galaxy13.server.security.UserPrincipal;
import com.galaxy13.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorizations endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> register(
            @Valid @RequestBody AuthDto.RegisterRequest request) {
        AuthDto.AuthResponse authResponse = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Register successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> refresh(
            @Valid @RequestBody AuthDto.RefreshTokenRequest request) {
        AuthDto.AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
    }

    @PostMapping("/tokens")
    @Operation(summary = "Create a new API token for client authentication")
    public ResponseEntity<ApiResponse<AuthDto.ApiTokenResponse>> createApiToken(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody AuthDto.ApiTokenRequest request) {
        AuthDto.ApiTokenResponse response = authService.createApiToken(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "API token created"));
    }

    @GetMapping("/tokens")
    @Operation(summary = "List user's API tokens")
    public ResponseEntity<ApiResponse<List<AuthDto.ApiTokenResponse>>> getApiTokens(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<AuthDto.ApiTokenResponse> tokens = authService.getApiTokens(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @DeleteMapping("/tokens/{tokenId}")
    @Operation(summary = "Revoke an API token")
    public ResponseEntity<ApiResponse<Void>> revokeApiToken(
            @AuthenticationPrincipal UserPrincipal user, @PathVariable UUID tokenId) {
        authService.revokeApiToken(user.getId(), tokenId);
        return ResponseEntity.ok(ApiResponse.success(null, "API token revoked"));
    }
}
