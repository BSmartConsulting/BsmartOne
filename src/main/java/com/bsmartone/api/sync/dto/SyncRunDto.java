package com.bsmartone.api.sync.dto;

import com.bsmartone.api.sync.model.SyncRun;
import com.bsmartone.api.sync.model.SyncRunStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SyncRunDto {
    Long id;
    String entityName;
    SyncRunStatus status;
    Integer processedCount;
    Integer totalExpected;
    BigDecimal progressPercent;
    Integer currentOffset;
    LocalDateTime startedAt;
    LocalDateTime endedAt;
    String oracleUploadStatus;
    String errorMessage;

    public static SyncRunDto fromEntity(SyncRun run) {
        return SyncRunDto.builder()
            .id(run.getId())
            .entityName(run.getEntityName())
            .status(run.getStatus())
            .processedCount(run.getProcessedCount())
            .totalExpected(run.getTotalExpected())
            .progressPercent(run.getProgressPercent())
            .currentOffset(run.getCurrentOffset())
            .startedAt(run.getStartedAt())
            .endedAt(run.getEndedAt())
            .oracleUploadStatus(run.getOracleUploadStatus())
            .errorMessage(run.getErrorMessage())
            .build();
    }
}
