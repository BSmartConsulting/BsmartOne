package com.bsmartone.api.sync.repository;

import com.bsmartone.api.sync.model.EntityConfiguration;
import com.bsmartone.api.sync.model.EntityFieldMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityFieldMappingRepository extends JpaRepository<EntityFieldMapping, Long> {
    List<EntityFieldMapping> findByEntityConfigurationAndEnabledTrueOrderByFieldOrderAsc(EntityConfiguration entityConfiguration);
}
