package com.nebulak.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nebulak.model.User;
import com.nebulak.dto.UserDetailsDTO;

public interface UserRepository extends JpaRepository< User ,Long> {
	public User findByEmail(String email);
	public User findByActivationToken(String token);
}
