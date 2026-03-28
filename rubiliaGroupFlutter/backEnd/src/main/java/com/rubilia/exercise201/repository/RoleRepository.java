package com.rubilia.exercise201.repository;

import com.rubilia.exercise201.entity.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query("SELECT r FROM Role r WHERE r.role_name = :roleName")
    Optional<Role> findByRoleName(@Param("roleName") String roleName);
}