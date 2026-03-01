package com.galaxy13.server.service;

import com.galaxy13.server.dto.UserDto;
import com.galaxy13.server.exception.BadRequestException;
import com.galaxy13.server.exception.ResourceNotFoundException;
import com.galaxy13.server.model.User;
import com.galaxy13.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final ConversionService conversionService;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow( () ->
                new ResourceNotFoundException("User not found with id: " + id)
        );
//        UserDto dto = conversionService.convert(user, UserDto.class);
//        dto.setTotalSaves(gameSaveService.getUserSaveCount(user.getId()));
//        dto.setTotalStorage(gameSaveService.getUserTotalStorage(user.getId()));
        return conversionService.convert(user, UserDto.class);
    }

    @Transactional
    public UserDto updateUser(UUID id, UserDto.UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found with id: " + id));
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        userRepository.save(user);
        return conversionService.convert(user, UserDto.class);
    }

    @Transactional
    public void updatePassword(UUID id, UserDto.ChangePasswordRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new  ResourceNotFoundException("User not found with id: " + id));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
