package com.duikt.fittrack.service;

import com.duikt.fittrack.domain.ProgressTrackerDTO;

import java.util.List;
import java.util.UUID;

public interface ProgressTrackerService {
    List<ProgressTrackerDTO> findAllProgressEntries();
    ProgressTrackerDTO findProgressEntryById(UUID id);
    ProgressTrackerDTO createProgressEntry(ProgressTrackerDTO progressTrackerDTO, UUID userId);
    ProgressTrackerDTO updateProgressEntry(UUID id, ProgressTrackerDTO progressTrackerDTO);
    void deleteProgressEntry(UUID id);
}
