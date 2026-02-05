package com.galaxy13.server.exception;

import java.util.UUID;

public class GameInfoNotFound extends RuntimeException {
    public GameInfoNotFound(UUID id) {
        super("Game info with id " + id + " not found");
    }
}
