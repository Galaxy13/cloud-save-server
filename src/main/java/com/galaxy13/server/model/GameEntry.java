package com.galaxy13.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GameEntry {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_sync")
    private Timestamp lastModified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "info_id", nullable = false)
    private GameInfo gameInfo;

    @OneToMany()
    private Set<FileMetadata> files;

    @Column(name = "version")
    private String version;
}
