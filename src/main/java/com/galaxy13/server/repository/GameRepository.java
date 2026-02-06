package com.galaxy13.server.repository;

import com.galaxy13.server.model.Game;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    Optional<Game> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Game> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT g FROM Game g where g.name LIKE %:search% OR g.slug LIKE %:search%")
    Page<Game> searchGames(String search, Pageable pageable);
}
