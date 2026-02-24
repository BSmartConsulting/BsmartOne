package com.bsmartone.api.sync.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "raw_record")
public class RawRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String entityName;
    private String country;
    @Column(name = "source_offset")
    private Integer sourceOffset;
    private Integer sourceIndex;
    @Column(columnDefinition = "TEXT")
    private String recordText;
    private LocalDateTime createdAt;
}
