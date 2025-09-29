package com.galaxy13.server.repository;

import com.galaxy13.server.model.GameEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameEntryRepository extends JpaRepository<GameEntry, UUID> {
    Optional<GameEntry> findById(UUID gameId);

    Optional<GameEntry> findByGameInfoId(UUID gameInfoId);
}
