package com.bsmartone.api.sync.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "entity_configuration")
public class EntityConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String entityName;
    private String endpointPath;
    private String countryField;
    private String fileName;
    private String delimiter;
    private String lineSeparator;
    private Boolean enabled;
    @Column(columnDefinition = "TEXT")
    private String notes;
}
