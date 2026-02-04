package com.galaxy13.server.repository;

import com.galaxy13.server.model.SaveHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SaveHistoryRepository extends JpaRepository<SaveHistory, UUID> {

    List<SaveHistory> findByGameSaveIdOrderByVersionDesc(UUID gameSaveId);

    void deleteByGameSaveId(UUID gameSaveId);
}
