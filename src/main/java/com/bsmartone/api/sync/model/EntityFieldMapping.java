package com.bsmartone.api.sync.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "entity_field_mapping")
public class EntityFieldMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_configuration_id", nullable = false)
    private EntityConfiguration entityConfiguration;

    private Integer fieldOrder;
    private String fieldName;
    private String jsonPath;
    private String defaultValue;
    private Boolean enabled;
}
