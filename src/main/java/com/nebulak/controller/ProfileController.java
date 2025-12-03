package com.nebulak.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nebulak.config.JwtProvider;
import com.nebulak.dto.UserCourseEnrollmentsDTO;
import com.nebulak.dto.UserProfileDTO;
import com.nebulak.model.User;
import com.nebulak.repository.UserRepository;
import com.nebulak.service.S3Service;


@RestController
@RequestMapping("/api/user")
public class ProfileController {
	
	private final JwtProvider jwtProvider;
	private final S3Service s3Service;
	private final CustomUserServiceImplementation customUserServiceImplementation;
	private final UserRepository userRepository;
	
	public ProfileController(
		CustomUserServiceImplementation customUserServiceImplementation, 
		JwtProvider jwtProvider,
		S3Service s3Service,
		UserRepository userRepository
	) {
		this.s3Service = s3Service;
		this.customUserServiceImplementation = customUserServiceImplementation;
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
	}
	
	@GetMapping("/profile")
	public ResponseEntity<UserProfileDTO> getCurrentUserProfile(@RequestHeader("Authorization") String authHeader) {
	    String jwt = null;
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        jwt = authHeader; 
	    } else {
	        return ResponseEntity.status(401).build();
	    }
	    String email = jwtProvider.getEmailFromToken(jwt);
	    User userDetails = customUserServiceImplementation.loadUserDetailsByUsername(email);
	    UserProfileDTO userProfile = new UserProfileDTO(
	        userDetails.getFirstName(),
	        userDetails.getLastName(),
	        userDetails.getEmail(),
	        userDetails.getMobile(),
	        userDetails.getRoles(),
	        userDetails.getProfile_pic()
	   
	    );

	    return ResponseEntity.ok(userProfile);
	}
	@GetMapping("/enrollments")
	public ResponseEntity<UserCourseEnrollmentsDTO> getCurrentUserEnrollments(@RequestHeader("Authorization") String authHeader) {
	    String jwt = null;
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        jwt = authHeader; 
	    } else {
	        return ResponseEntity.status(401).build();
	    }
	    String email = jwtProvider.getEmailFromToken(jwt);
	    User userDetails = customUserServiceImplementation.loadUserDetailsByUsername(email);
	    UserCourseEnrollmentsDTO userEnrollments = new UserCourseEnrollmentsDTO(
	        userDetails.getEnrollments()
	    );

	    return ResponseEntity.ok(userEnrollments);
	}
	
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {
    	String jwt = null;
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        jwt = authHeader;
	    } else {
	        return ResponseEntity.status(401).body("Authorization header is missing or invalid.");
	    }
	    try {
			String email = jwtProvider.getEmailFromToken(jwt);
			User user = userRepository.findByEmail(email);
			if (user == null) {
				return ResponseEntity.status(404).body("User not found.");
			}
			String oldProfilePicUrl = user.getProfile_pic();
			String newProfilePicUrl = s3Service.uploadFile(file);
			user.setProfile_pic(newProfilePicUrl);
			userRepository.save(user);
			if (oldProfilePicUrl != null && !oldProfilePicUrl.isEmpty()) {
				s3Service.deleteFile(oldProfilePicUrl);
			}
            return ResponseEntity.ok("Successfully Uploaded. Profile picture updated.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during file upload: " + e.getMessage());
        }
    }
    
}