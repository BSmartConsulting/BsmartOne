package com.bsmartone.api.sync.controller;

import com.bsmartone.api.sync.dto.SyncRunDto;
import com.bsmartone.api.sync.model.SyncRun;
import com.bsmartone.api.sync.repository.SyncRunRepository;
import com.bsmartone.api.sync.service.SyncOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sync")
public class SyncController {

    private final SyncOrchestratorService syncOrchestratorService;
    private final SyncRunRepository syncRunRepository;

    public SyncController(SyncOrchestratorService syncOrchestratorService, SyncRunRepository syncRunRepository) {
        this.syncOrchestratorService = syncOrchestratorService;
        this.syncRunRepository = syncRunRepository;
    }

    @PostMapping("/{entityName}/start")
    public ResponseEntity<SyncRunDto> start(@PathVariable String entityName) {
        SyncRun run = syncOrchestratorService.createRun(entityName);
        syncOrchestratorService.executeRun(run.getId());
        return ResponseEntity.accepted().body(SyncRunDto.fromEntity(run));
    }

    @GetMapping("/{runId}")
    public ResponseEntity<SyncRunDto> get(@PathVariable Long runId) {
        SyncRun run = syncRunRepository.findById(runId).orElseThrow();
        return ResponseEntity.ok(SyncRunDto.fromEntity(run));
    }
}
