package com.galaxy13.server.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "save_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "save_id", nullable = false)
    private GameSave gameSave;

    @Column(name = "file_key", nullable = false, length = 500)
    private String fileKey;

    @Column(name = "file_size", nullable = false)
    private Long size;

    @Column(nullable = false, length = 64)
    private String checksum;

    @Column(nullable = false)
    private Integer version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
