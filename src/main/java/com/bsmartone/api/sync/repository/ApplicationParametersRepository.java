package com.bsmartone.api.sync.repository;

import com.bsmartone.api.sync.model.ApplicationParameters;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationParametersRepository extends JpaRepository<ApplicationParameters, Long> {
    Optional<ApplicationParameters> findFirstByEnabledTrue();
}
