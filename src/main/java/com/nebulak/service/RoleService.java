package com.nebulak.service;

import com.nebulak.model.Role;
import com.nebulak.model.User;             // Import User model
import com.nebulak.model.UserRoles;
import com.nebulak.repository.RoleRepository;
import com.nebulak.repository.UserRepository;     // Import UserRepository
import com.nebulak.repository.UserRolesRepository; // Import UserRolesRepository
import com.nebulak.exception.UserException;    // Assuming you have this custom exception

import java.util.List;
import java.util.Optional;                  // Import Optional

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;         // Added
    private final UserRolesRepository userRolesRepository; // Added

    // 1. Update Constructor to Inject new Repositories
    @Autowired
    public RoleService(RoleRepository roleRepository, 
                       UserRepository userRepository, 
                       UserRolesRepository userRolesRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRolesRepository = userRolesRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    // 2. Completed assignRoleToUser method
	public UserRoles assignRoleToUser(Long userId, Long roleId) throws UserException {
		
        // Fetch User by ID
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new UserException("User not found with ID: " + userId);
        }
        User user = userOpt.get();

        // Fetch Role by ID
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new UserException("Role not found with ID: " + roleId);
        }
        Role role = roleOpt.get();
        
        // Create and save the UserRoles assignment
        UserRoles userRole = new UserRoles();
        userRole.setUser(user);
        userRole.setRole(role);
        
		return userRolesRepository.save(userRole);
	}
}