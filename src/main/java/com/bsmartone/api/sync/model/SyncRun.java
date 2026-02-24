package com.bsmartone.api.sync.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sync_run")
public class SyncRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String entityName;
    @Enumerated(EnumType.STRING)
    private SyncRunStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer totalExpected;
    private Integer processedCount;
    private Integer currentOffset;
    private BigDecimal progressPercent;
    private String csvPath;
    private String oracleUploadStatus;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}
