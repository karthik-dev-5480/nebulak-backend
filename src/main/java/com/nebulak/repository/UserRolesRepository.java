package com.nebulak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nebulak.model.UserRoles;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {
    // Optional: Add a method to check if the assignment already exists
    // UserRoles findByUserIdAndRoleId(Long userId, Long roleId);
}