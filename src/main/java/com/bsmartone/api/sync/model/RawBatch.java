package com.bsmartone.api.sync.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "raw_batch")
public class RawBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String entityName;
    private String country;
    @Column(name = "source_offset")
    private Integer sourceOffset;
    private Integer batchSize;
    private Integer recordsCount;
    @Column(columnDefinition = "TEXT")
    private String rawJson;
    private LocalDateTime createdAt;
}
