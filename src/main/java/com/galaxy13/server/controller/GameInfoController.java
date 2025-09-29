package com.galaxy13.server.controller;

import com.galaxy13.server.service.GameInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameInfoController {

    private final GameInfoService gameInfoService;
}
