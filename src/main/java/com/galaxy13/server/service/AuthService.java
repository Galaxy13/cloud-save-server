package com.galaxy13.server.service;

import com.galaxy13.server.config.JwtConfigurationProperties;
import com.galaxy13.server.dto.AuthDto;
import com.galaxy13.server.dto.UserDto;
import com.galaxy13.server.exception.BadRequestException;
import com.galaxy13.server.exception.ResourceNotFoundException;
import com.galaxy13.server.helper.ApiTokenHasher;
import com.galaxy13.server.model.ApiToken;
import com.galaxy13.server.model.Role;
import com.galaxy13.server.model.User;
import com.galaxy13.server.repository.ApiTokenRepository;
import com.galaxy13.server.repository.UserRepository;
import com.galaxy13.server.security.JWTUtils;
import com.galaxy13.server.security.UserPrincipal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final ApiTokenRepository apiTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final ApiTokenHasher apiTokenHasher;

    private final AuthenticationManager authenticationManager;

    private final JWTUtils jwtUtils;

    private final ConversionService conversionService;

    private final JwtConfigurationProperties properties;

    @Transactional
    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user =
                userRepository
                        .findById(userPrincipal.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        String accessToken = jwtUtils.generateToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(userPrincipal.getUsername());

        return AuthDto.AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(properties.getExpiration())
                .user(conversionService.convert(user, UserDto.class))
                .build();
    }

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        User user =
                User.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .passwordHash(passwordEncoder.encode(request.getPassword()))
                        .role(Role.USER)
                        .isActive(true)
                        .build();

        user = userRepository.save(user);

        String accessToken = jwtUtils.generateToken(user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        return AuthDto.AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(properties.getExpiration())
                .user(conversionService.convert(user, UserDto.class))
                .build();
    }

    @Transactional
    public AuthDto.AuthResponse refreshToken(AuthDto.RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String tokenType = jwtUtils.extractTokenType(refreshToken);
        if (!tokenType.equals("refresh")) {
            throw new BadRequestException("Invalid token type");
        }
        String username = jwtUtils.extractUsername(refreshToken);
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtUtils.generateToken(user.getUsername());
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        return AuthDto.AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(properties.getExpiration())
                .user(conversionService.convert(user, UserDto.class))
                .build();
    }

    @Transactional
    public AuthDto.ApiTokenResponse createApiToken(UUID userId, AuthDto.ApiTokenRequest request) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String rawToken = generateSecureRandom();

        Instant expiresAt = null;
        if (request.getExpirationDays() != null && request.getExpirationDays() > 0) {
            expiresAt = Instant.now().plus(request.getExpirationDays(), ChronoUnit.DAYS);
        }
        ApiToken apiToken =
                ApiToken.builder()
                        .user(user)
                        .tokenHash(apiTokenHasher.hash(rawToken))
                        .name(request.getName())
                        .expiresAt(expiresAt)
                        .isActive(true)
                        .build();
        apiToken = apiTokenRepository.save(apiToken);

        return AuthDto.ApiTokenResponse.builder()
                .id(apiToken.getId().toString())
                .token("gst_" + rawToken)
                .name(apiToken.getName())
                .expiresAt(expiresAt != null ? expiresAt.toString() : null)
                .createdAt(apiToken.getCreatedAt().toString())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AuthDto.ApiTokenResponse> getApiTokens(UUID userId) {
        return apiTokenRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(
                        token ->
                                AuthDto.ApiTokenResponse.builder()
                                        .id(token.getId().toString())
                                        .token(null)
                                        .name(token.getName())
                                        .expiresAt(
                                                token.getExpiresAt() != null
                                                        ? token.getExpiresAt().toString()
                                                        : null)
                                        .createdAt(token.getCreatedAt().toString())
                                        .build())
                .toList();
    }

    @Transactional
    public void revokeApiToken(UUID userId, UUID tokenId) {
        ApiToken token =
                apiTokenRepository
                        .findById(tokenId)
                        .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
        if (!token.getUser().getId().equals(userId)) {
            throw new BadRequestException("Token does not belong to user");
        }
        token.setActive(false);
        apiTokenRepository.save(token);
    }

    private String generateSecureRandom() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
