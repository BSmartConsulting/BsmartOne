package com.bsmartone.api.repository.masterData;

import com.bsmartone.api.model.masterData.UserRole;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    void deleteByUserId(Long userId);

    Set<UserRole> findByUserId(Long userId);
    
    boolean existsByUser_IdAndRole_Id(Long userId, Long roleId);
}
