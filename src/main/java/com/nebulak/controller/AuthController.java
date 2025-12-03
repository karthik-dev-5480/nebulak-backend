package com.nebulak.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nebulak.config.JwtProvider;
import com.nebulak.dto.AssignRoleRequest;
import com.nebulak.dto.AuthResponse;
import com.nebulak.dto.AuthenticationResult;
import com.nebulak.dto.LoginRequest;
import com.nebulak.dto.UserDTO;
import com.nebulak.dto.UserDetailsDTO;
import com.nebulak.exception.UserException;
import com.nebulak.model.Role;
import com.nebulak.model.User;
import com.nebulak.model.UserRoles;
import com.nebulak.repository.UserRepository;
import com.nebulak.service.EmailService;
import com.nebulak.service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private UserRepository userRepository;
	private JwtProvider jwtProvider;
	private PasswordEncoder passwordEncoder;
	private CustomUserServiceImplementation customUserServiceImplementation;
	private EmailService emailService;
	private RoleService roleService;
	
	
	public AuthController(UserRepository userRepository,CustomUserServiceImplementation customUserServiceImplementation , PasswordEncoder passwordEncoder, JwtProvider jwtProvider, EmailService emailService,RoleService roleService) {
		
		this.userRepository=userRepository;
		this.customUserServiceImplementation=customUserServiceImplementation;
		this.passwordEncoder= passwordEncoder;
		this.jwtProvider=jwtProvider;
		this.emailService=emailService;
		this.roleService = roleService;
	}
	
	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse>createUserHandler(@RequestBody User user)throws UserException{
		String email=user.getEmail();
		String password=user.getPassword();
		String firstNString=user.getFirstName();
		String lastNString=user.getLastName();
		String activationToken = UUID.randomUUID().toString();
	    
	    
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date());
	    calendar.add(Calendar.HOUR_OF_DAY, 24);
		User isEmailExist=userRepository.findByEmail(email);
		if(isEmailExist!=null) {
			throw new UserException("Email Not Found");
		}
		
		//cache
		
		User savedUser=customUserServiceImplementation.createUser(email,password,firstNString,lastNString,activationToken,calendar);
		
		
		try {
	        String activationLink = "http://localhost:5454/auth/activate/" + activationToken; // Replace with your actual public domain
	        emailService.sendActivationEmail(savedUser.getEmail(), activationLink);
	    } catch (MailException e) {
	        System.err.println("Failed to send activation email: " + e.getMessage());
	    }
		
		AuthResponse authResponse = new AuthResponse(null, "Signup Success. Please check your email to activate your account.");
	    return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
		
	}
	
	
	@GetMapping("/activate/{token}")
	public ResponseEntity<String> activateAccount(@PathVariable String token) {
	    User user = userRepository.findByActivationToken(token);
	    
	    
	    if (user == null) {
	        return new ResponseEntity<>("Activation failed: Invalid token.", HttpStatus.BAD_REQUEST);
	    }
	    
	    if (user.getTokenExpiryDate().before(new Date())) {
	        return new ResponseEntity<>("Activation failed: Token has expired.", HttpStatus.BAD_REQUEST);
	    }
	    
	    String email=user.getEmail();
	    
	    Boolean enable=customUserServiceImplementation.activateUser(email);

	    if(enable) {
		    return new ResponseEntity<>("Account successfully activated! You can now log in.", HttpStatus.OK);

	    }

	    return new ResponseEntity<>("Activation failed: User not found.", HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) {
	    String username = loginRequest.getEmail();
	    String password = loginRequest.getPassword();

	    AuthenticationResult result = authenticate(username, password);
	    System.out.println(result);
	    if (!result.isSuccess()) {
	        AuthResponse errorResponse = new AuthResponse(null, result.getErrorMessage());
	        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); 
	    }

	    Authentication authentication = result.getAuthentication();
	    System.out.println("details"+authentication);
	   
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    String token = jwtProvider.generateToken(authentication);
	    AuthResponse authResponse = new AuthResponse(token, "Signin Success");
	    
	    return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
	}
	

	
	public AuthenticationResult authenticate(String username, String password) {
	    UserDetails userDetails = customUserServiceImplementation.loadUserByUsername(username);

	    if (userDetails == null) {
	        return new AuthenticationResult("User Not Found");
	    }

	    System.out.println("auth"+userDetails);
	    if (!userDetails.isEnabled()) {
	        System.out.println(userDetails.isEnabled());
	        return new AuthenticationResult("Please activate user");
	    }

	    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
	        return new AuthenticationResult("Invalid password..");
	    }
	    
	    Authentication successAuth = new UsernamePasswordAuthenticationToken(
	        userDetails, 
	        null, 
	        userDetails.getAuthorities()
	    );

	    return new AuthenticationResult(successAuth);
	}
	@PostMapping("/role/add") // e.g., POST to /api/roles/add
    public ResponseEntity<Role> addRole(@RequestBody Role role) {
        // In a real application, consider using a RoleCreationDTO instead of the Role entity directly.
        Role newRole = roleService.createRole(role);
        return new ResponseEntity<>(newRole, HttpStatus.CREATED);
    }
	
	@GetMapping("/roles/getallroles") // New endpoint to get all roles
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
	
	@GetMapping("/users/getallusersdata")
    public ResponseEntity<Page<UserDTO>> getAllUsersData(Pageable pageable) {
        
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserDTO> userDTOPage = userPage.map(UserDTO::new);
        return new ResponseEntity<>(userDTOPage, HttpStatus.OK);
    }
	@GetMapping("/users/getallusers")
	public ResponseEntity<Page<UserDTO>> getAllUsers(
	        @RequestParam(defaultValue = "0") int page, // FIX: Default to 0-based index
	        @RequestParam(defaultValue = "10") int size
	) {
	    // FIX: Client sends 0-based page number, so use it directly.
	    // We ensure the page number is not negative just in case.
	    int zeroBasedPage = Math.max(0, page); 

	    // Call the service layer method with pagination parameters
	    // Note: The service layer method implementation is assumed to correctly use PageRequest.of(zeroBasedPage, size)
	    Page<UserDTO> usersPage = customUserServiceImplementation.findUsersPaginated(zeroBasedPage, size);
	    
	    return ResponseEntity.ok(usersPage);
	}
	@PostMapping("/users/assignrole")
    public ResponseEntity<String> assignRoleHandler(@RequestBody AssignRoleRequest request) {
        try {
            UserRoles userRoleAssignment = roleService.assignRoleToUser(
                request.getUserId(), 
                request.getRoleId()
            );
            
            String successMessage = String.format(
                "Role (ID: %d) successfully assigned to User (ID: %d). Assignment ID: %d",
                request.getRoleId(),
                request.getUserId(),
                userRoleAssignment.getId()
            );
            
            return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
        } catch (UserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle other unexpected exceptions
            return new ResponseEntity<>("An internal error occurred during role assignment.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
