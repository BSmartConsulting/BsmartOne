package com.bsmartone.api.sync.repository;

import com.bsmartone.api.sync.model.RawBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawBatchRepository extends JpaRepository<RawBatch, Long> {}
