package com.galaxy13.server.repository;

import com.galaxy13.server.model.GameInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameInfoRepository extends JpaRepository<GameInfo, UUID> {
}
