package com.galaxy13.server.controller;

import com.galaxy13.server.service.GameEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameEntryController {

    private final GameEntryService gameEntryService;
}
