package com.duikt.fittrack.API;

import com.duikt.fittrack.domain.ProgressTrackerDTO;
import com.duikt.fittrack.mapper.ProgressTrackerMapper;
import com.duikt.fittrack.service.ProgressTrackerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class ProgressTrackerRestController {

    private final ProgressTrackerService progressTrackerService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ProgressTrackerDTO>> getProgressEntries() {
        return ResponseEntity.ok(progressTrackerService.findAllProgressEntries());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProgressTrackerDTO> getProgressEntry(@PathVariable UUID id) {
        return ResponseEntity.ok(progressTrackerService.findProgressEntryById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProgressTrackerDTO> createProgressEntry(@Valid @RequestBody ProgressTrackerDTO progressTrackerDTO,
                                                                  @RequestParam UUID userId) {
        return new ResponseEntity<>(progressTrackerService.createProgressEntry(progressTrackerDTO, userId),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProgressTrackerDTO> updateProgressEntry(@PathVariable UUID id,
                                                                  @Valid @RequestBody ProgressTrackerDTO progressTrackerDTO) {
        return ResponseEntity.ok(progressTrackerService.updateProgressEntry(id, progressTrackerDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProgressEntry(@PathVariable UUID id) {
        progressTrackerService.deleteProgressEntry(id);
    }
}
