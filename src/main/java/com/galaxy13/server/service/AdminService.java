package com.galaxy13.server.service;

import com.galaxy13.server.dto.GameDto;
import com.galaxy13.server.dto.UserDto;
import com.galaxy13.server.exception.BadRequestException;
import com.galaxy13.server.exception.ResourceNotFoundException;
import com.galaxy13.server.model.Role;
import com.galaxy13.server.model.User;
import com.galaxy13.server.repository.GameRepository;
import com.galaxy13.server.repository.GameSaveRepository;
import com.galaxy13.server.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final UserRepository userRepository;

    private final GameRepository gameRepository;

    private final GameSaveRepository gameSaveRepository;

    private final PasswordEncoder passwordEncoder;

    private final ConversionService conversionService;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalGames", gameRepository.count());
        stats.put("totalSavedGames", gameSaveRepository.count());
        stats.put(
                "activeUsers",
                userRepository.findByIsActiveTrue(Pageable.unpaged()).getTotalElements());
        return stats;
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository
                .findAll(pageable)
                .map(
                        user -> {
                            UserDto dto = conversionService.convert(user, UserDto.class);
                            dto.setTotalSaves(gameSaveRepository.countByUserId(user.getId()));
                            dto.setTotalStorage(
                                    gameSaveRepository.getTotalFileSizeByUserId(user.getId()));
                            return dto;
                        });
    }

    public Page<UserDto> searchUsers(String search, Pageable pageable) {
        return userRepository
                .searchUsers(search, pageable)
                .map(user -> conversionService.convert(user, UserDto.class));
    }

    public UserDto getUser(UUID userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserDto dto = conversionService.convert(user, UserDto.class);
        dto.setTotalSaves(gameSaveRepository.countByUserId(user.getId()));
        dto.setTotalStorage(gameSaveRepository.getTotalFileSizeByUserId(user.getId()));
        return dto;
    }

    @Transactional
    public UserDto updateUser(UUID userId, String email, String role, Boolean isActive) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new BadRequestException("Email already exists");
            }
            user.setEmail(email);
        }

        if (role != null) {
            try {
                user.setRole(Role.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid role: " + role);
            }
        }

        if (isActive != null) {
            user.setIsActive(isActive);
        }
        user = userRepository.save(user);
        log.info("User {} updated", user.getUsername());

        return conversionService.convert(user, UserDto.class);
    }

    @Transactional
    public void resetUserPassword(UUID userId, String newPassword) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Admin reset password for user: {}", user.getUsername());
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole().equals(Role.ADMIN)) {
            long adminCount =
                    userRepository.findAll().stream()
                            .filter(u -> u.getRole().equals(Role.ADMIN) && u.getIsActive())
                            .count();
            if (adminCount <= 1) {
                throw new BadRequestException("Cannot delete the last adin user");
            }
        }
        userRepository.delete(user);
        log.info("Admin deleted user {}", user.getUsername());
    }

    public Page<GameDto> getAllGamesAdmin(Pageable pageable) {
        return gameRepository
                .findAll(pageable)
                .map(
                        game -> {
                            GameDto dto = conversionService.convert(game, GameDto.class);
                            dto.setSaveCount((long) game.getGameSaves().size());
                            return dto;
                        });
    }
}
