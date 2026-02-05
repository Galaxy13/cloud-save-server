package com.galaxy13.server.exception;

import java.util.UUID;

public class GameEntryNotFound extends RuntimeException {
    public GameEntryNotFound(UUID id) {
        super("Game entry with id " + id + " not found");
    }
}
