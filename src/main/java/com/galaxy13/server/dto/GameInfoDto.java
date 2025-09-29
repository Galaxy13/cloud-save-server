package com.galaxy13.server.dto;


import java.util.UUID;

public record GameInfoDto(UUID id,
                          String name,
                          String description,
                          String imageUrl, String iconUrl) {
}
