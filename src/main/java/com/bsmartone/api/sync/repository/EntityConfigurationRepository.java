package com.bsmartone.api.sync.repository;

import com.bsmartone.api.sync.model.EntityConfiguration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityConfigurationRepository extends JpaRepository<EntityConfiguration, Long> {
    Optional<EntityConfiguration> findByEntityNameAndEnabledTrue(String entityName);
}
