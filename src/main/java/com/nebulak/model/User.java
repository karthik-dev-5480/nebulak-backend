package com.nebulak.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String firstName;
	
	private String lastName;
	
	private String displayName;
	
	private String email;
	
	private String password;
	
	
	private String mobile;
	
	private String profile_pic;
	
	private String activationToken;
	
	private Date tokenExpiryDate;
	
	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Course> ownedCourses = new ArrayList<>();
	
	public List<Course> getOwnedCourses() {
		return ownedCourses;
	}

	public void setOwnedCourses(List<Course> ownedCourses) {
		this.ownedCourses = ownedCourses;
	}
	
	// **Renamed field to match Spring Security convention**
	private boolean enabled;

	// **New Getter/Setter**
	
	
	
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Cart cart;

    // A user can place multiple orders
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();
    
    // A user can be enrolled in multiple courses
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserCourseEnrollment> enrollments = new ArrayList<>();
	
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserRoles> roles = new ArrayList<>();
	
	private LocalDateTime createdAt;
	
	public User() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getProfile_pic() {
		return profile_pic;
	}

	public void setProfile_pic(String profile_pic) {
		this.profile_pic = profile_pic;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	

	public List<UserCourseEnrollment> getEnrollments() {
		return enrollments;
	}

	public void setEnrollments(List<UserCourseEnrollment> enrollments) {
		this.enrollments = enrollments;
	}
	
	

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}
	
	

	public String getActivationToken() {
		return activationToken;
	}

	public void setActivationToken(String activationToken) {
		this.activationToken = activationToken;
	}

	public Date getTokenExpiryDate() {
		return tokenExpiryDate;
	}

	public void setTokenExpiryDate(Date tokenExpiryDate) {
		this.tokenExpiryDate = tokenExpiryDate;
	}


	
	public boolean isEnabled() {
	    // Since 'enabled' is a primitive 'boolean', it cannot be null.
	    // The value mapped from DB (0 -> false, 1 -> true) is returned directly.
	    return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
	    this.enabled = enabled;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<UserRoles> getRoles() {
		return roles;
	}

	public void setRoles(List<UserRoles> roles) {
		this.roles = roles;
	}
	

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public User(Long id, String firstName, String lastName, String email, String password,String mobile,
			String profile_pic, String activationToken, Date tokenExpiryDate, boolean enabled, Cart cart,
			List<Order> orders, List<UserCourseEnrollment> enrollments, List<UserRoles> roles,
			LocalDateTime createdAt, String displayName,List<Course> ownedCourses) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		
		this.mobile = mobile;
		this.profile_pic = profile_pic;
		this.activationToken = activationToken;
		this.tokenExpiryDate = tokenExpiryDate;
		this.enabled = enabled;
		this.cart = cart;
		this.orders = orders;
		this.enrollments = enrollments;
		this.roles = roles;
		this.createdAt = createdAt;
		this.displayName=displayName;
		this.ownedCourses=ownedCourses;
	}

	
	
	
	

}
