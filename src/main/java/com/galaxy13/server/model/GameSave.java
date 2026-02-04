package com.galaxy13.server.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "game_saves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSave {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "save_name", nullable = false)
    private String saveName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_key", nullable = false, length = 500)
    private String fileKey;

    @Column(name = "file_size", nullable = false)
    private Long size;

    @Column(nullable = false, length = 64)
    private String checksum;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsondb")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_auto_save")
    @Builder.Default
    private boolean isAutoSave = true;

    @Column
    @Builder.Default
    private Integer version = 1;

    @OneToMany(mappedBy = "gameSave", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SaveHistory> history = new ArrayList<>();
}
