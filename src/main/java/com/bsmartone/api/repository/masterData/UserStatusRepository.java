package com.bsmartone.api.repository.masterData;

import com.bsmartone.api.model.masterData.UserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {

    Optional<UserStatus> findByCode(String code);

    public Optional<UserStatus> findByDescription(String activo);

    public List<UserStatus> findAllByDeletedFalse();

}
