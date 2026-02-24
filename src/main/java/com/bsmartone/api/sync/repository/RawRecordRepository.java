package com.bsmartone.api.sync.repository;

import com.bsmartone.api.sync.model.RawRecord;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawRecordRepository extends JpaRepository<RawRecord, Long> {
    List<RawRecord> findByEntityNameOrderByIdAsc(String entityName, Pageable pageable);
    long countByEntityName(String entityName);
}
