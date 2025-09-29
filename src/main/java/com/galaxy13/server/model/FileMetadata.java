package com.galaxy13.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.sql.Timestamp;

@Entity
public class FileMetadata {

    @Id
    public Long id;

    @Column(name = "filename", nullable = false)
    private String fileName;

    @Column(name = "relative_path", nullable = false)
    private String relativePath;

    @Column(name = "ext", nullable = false)
    private String extension;

    @Column(name = "lastModified")
    private Timestamp lastModified;

    @Column(name = "s3_key", nullable = false, unique = true)
    private String s3Key;

    @Column(name = "size_bytes")
    private Long sizeBytes;
}
