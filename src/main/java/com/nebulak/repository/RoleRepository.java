package com.nebulak.repository;

import com.nebulak.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Custom methods can be added here if needed, 
    // e.g., Role findByName(String name);
}