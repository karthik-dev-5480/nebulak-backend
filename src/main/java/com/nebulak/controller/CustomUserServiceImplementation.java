package com.nebulak.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nebulak.dto.UserDTO;
import com.nebulak.dto.UserDetailsDTO;
import com.nebulak.model.User;
import com.nebulak.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Service
public class CustomUserServiceImplementation implements UserDetailsService {
	
private UserRepository userRepository;
private PasswordEncoder passwordEncoder;

	
	public CustomUserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository=userRepository;
		this.passwordEncoder=passwordEncoder;
	}
	
	//@Cacheable(value = "authUsers", key = "#username")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    User user = userRepository.findByEmail(username);
	    if(user == null) {
	        throw new UsernameNotFoundException("User not found with email - " + username);
	    }
	    System.out.println(user);
	    List<GrantedAuthority> authorities = new ArrayList<>();
	    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
	        user.getEmail(),
	        user.getPassword(),
	        user.isEnabled(), 
	        true, 
	        true, 
	        true, 
	        authorities 
	    );
	    return userDetails;
	}
	
	//@Cacheable(value = "userEntityCache", key = "#username")
	public User loadUserDetailsByUsername(String username) throws UsernameNotFoundException {
		User user=userRepository.findByEmail(username);
		if(user==null) {
			throw new UsernameNotFoundException("User not found with email - "+username);
		}
		return user;
	}
	public List<UserDetailsDTO> getUserDetails() {
		// TODO Auto-generated method stub
		List<User> users = userRepository.findAll();
		
		List<UserDetailsDTO> userDetailsDTOs = users.stream()
		        .map(user -> {
		            UserDetailsDTO dto = new UserDetailsDTO();
		            // Map the ID
		            dto.setId(user.getId());
		            // Map the displayName
		            dto.setDisplayName(user.getDisplayName());
		            return dto;
		        })
		        .collect(Collectors.toList());

		    // 3. Return the list of DTOs
		    return userDetailsDTOs;
	}
	public Page<UserDTO> findUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> userEntityPage = userRepository.findAll(pageable);
        
        return userEntityPage.map(this::convertToUserDetailsDTO);
    }
   
    private UserDTO convertToUserDetailsDTO(User user) {
       
        return new UserDTO(user);
    }

    //@CacheEvict(value = "authUsers", key = "#email")
	public Boolean activateUser(String email) {
		User user=userRepository.findByEmail(email);
		user.setEnabled(true);
	    user.setActivationToken(null); 
	    user.setTokenExpiryDate(null); // Clear the expiry date
	    userRepository.save(user);
		return true;
	}

	//@Cacheable(value = "userEntityCache", key = "#email")
	public User createUser(String email, String password, String firstNString, String lastNString,
			String activationToken, Calendar calendar) {
		// TODO Auto-generated method stub
User createdUser=new User();
		
		createdUser.setDisplayName(firstNString+' '+ lastNString);
		createdUser.setEmail(email);
		createdUser.setPassword(passwordEncoder.encode(password));
		createdUser.setFirstName(firstNString);
		createdUser.setLastName(lastNString);
		createdUser.setEnabled(false);
		createdUser.setActivationToken(activationToken);
		createdUser.setTokenExpiryDate(calendar.getTime());
		
		User savedUser=userRepository.save(createdUser);
		return savedUser;
	}

	
}
