package com.galaxy13.server.converter;

import com.galaxy13.server.dto.UserDto;
import com.galaxy13.server.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User source) {
        return UserDto.builder()
                .id(source.getId().toString())
                .username(source.getUsername())
                .email(source.getEmail())
                .role(source.getRole().name())
                .createdAt(source.getCreatedAt())
                .lastLogin(source.getLastLogin())
                .isActive(source.getIsActive())
                .build();
    }
}
