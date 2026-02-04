package com.galaxy13.server.repository;

import com.galaxy13.server.model.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiTokenRepository extends JpaRepository<ApiToken, UUID> {

    Optional<ApiToken> findByTokenHash(String tokenHash);

    List<ApiToken> findByUserIdAndActiveTrue(UUID userId);

    @Modifying
    @Query("update ApiToken t SET t.lastUsed = :lastUsed WHERE t.id = :id")
    void updateLastUsed(UUID id, Instant lastUsed);

    @Modifying
    @Query("update ApiToken t set t.isActive = false where t.expiresAt < :now AND t.isActive = true")
    void deactivateExpiredTokens(Instant now);
}
