package com.galaxy13.server.repository;

import com.galaxy13.server.model.GameSave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameSaveRepository extends JpaRepository<GameSave, UUID> {

    Page<GameSave> findByUserId(UUID userId, Pageable pageable);

    Page<GameSave> findByUserIdAndGameId(UUID userId, UUID gameId, Pageable pageable);

    List<GameSave> findByUserIdAndGameSlug(UUID userId, String gameSlug);

    Optional<GameSave> findByIdAndGameId(UUID id, UUID gameId);

    @Query("SELECT gs FROM GameSave gs WHERE gs.user.id = :userId ORDER BY gs.updatedAt DESC")
    List<GameSave> findRecentSavesByUser(UUID userId, Pageable pageable);

    @Query("SELECT count(gs) FROM GameSave gs WHERE gs.user.id = :userId")
    long countByUserId(UUID userId);

    @Query("select sum(gs.size) FROM GameSave gs where gs.user.id = :userId")
    Long getTotalFileSizeByUserId(UUID userId);

    Optional<GameSave> findByChecksumAndUserId(String checksum, UUID userId);

    Optional<GameSave> findByIdAndUserId(UUID id, UUID userId);

    UUID userId(UUID userId);
}
