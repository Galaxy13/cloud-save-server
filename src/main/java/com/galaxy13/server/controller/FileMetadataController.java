package com.galaxy13.server.controller;

import com.galaxy13.server.service.FileMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FileMetadataController {

    private final FileMetadataService fileMetadataService;
}
