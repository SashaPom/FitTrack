package com.duikt.fittrack.API;

import com.duikt.fittrack.domain.NutritionLogDTO;
import com.duikt.fittrack.service.NutritionLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/nutrition")
@RequiredArgsConstructor
public class NutritionLogRestController {

    private final NutritionLogService nutritionLogService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<NutritionLogDTO>> getNutritionLogs() {
        return ResponseEntity.ok(nutritionLogService.findAllLogs());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<NutritionLogDTO> getNutritionLog(@PathVariable UUID id) {
        return ResponseEntity.ok(nutritionLogService.findLogById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<NutritionLogDTO> createNutritionLog(@Valid @RequestBody NutritionLogDTO nutritionLogDTO,
                                                              @RequestParam UUID userId) {
        return new ResponseEntity<>(nutritionLogService.createLog(nutritionLogDTO, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<NutritionLogDTO> updateNutritionLog(@PathVariable UUID id,
                                                              @Valid @RequestBody NutritionLogDTO nutritionLogDTO) {
        return ResponseEntity.ok(nutritionLogService.updateLog(id, nutritionLogDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNutritionLog(@PathVariable UUID id) {
        nutritionLogService.deleteLog(id);
    }
}
