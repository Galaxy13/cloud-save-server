package com.galaxy13.server.repository;

import com.galaxy13.server.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
}
