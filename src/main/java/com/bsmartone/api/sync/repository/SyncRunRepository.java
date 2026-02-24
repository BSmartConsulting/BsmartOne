package com.bsmartone.api.sync.repository;

import com.bsmartone.api.sync.model.SyncRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncRunRepository extends JpaRepository<SyncRun, Long> {}
